package io.github.junkfood.heal.ui.destination.feed


import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.junkfood.heal.App.Companion.context
import io.github.junkfood.heal.R
import io.github.junkfood.heal.database.Repository
import io.github.junkfood.heal.ui.common.SnackbarUtil.scope
import io.github.junkfood.heal.ui.common.SnackbarUtil.snackbarHostState
import io.github.junkfood.heal.util.DatabaseUtil
import io.github.junkfood.heal.util.PreferenceUtil
import io.github.junkfood.heal.util.TextUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

class FeedViewModel : ViewModel() {
    //    init { fetchPodcast() }
    private val mutableStateFlow = MutableStateFlow(FeedViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    val episodeAndRecordFlow = Repository.getEpisodeAndRecord().filterNotNull()

    private val TAG = "FeedViewModel"

    init {
        Log.d(TAG, "init")
        viewModelScope.launch {
            loadItems()
        }
    }

    suspend fun loadItems() {
        Repository.getPodcastsWithEpisodes()
            .distinctUntilChanged { old, new -> old.size == new.size }.collect {
                val feedItems: MutableList<FeedItem> = ArrayList()
                val podcastItems: MutableList<PodcastItem> = ArrayList()
                it.forEach { item ->
                    podcastItems.add(
                        PodcastItem(
                            podcastId = item.podcast.id,
                            title = item.podcast.title,
                            imageUrl = item.podcast.coverUrl
                        )
                    )
                    item.episodes.forEach { episode ->
                        feedItems.add(
                            FeedItem(
                                episodeId = episode.id,
                                imageUrl = episode.cover,
                                podcastTitle = item.podcast.title,
                                pubDate = episode.pubDate,
                                title = episode.title,
                                description = episode.description
                            )
                        )
                    }
                }
                feedItems.sortWith { o1: FeedItem, o2: FeedItem ->
                    TextUtil.compareDate(
                        o1.pubDate,
                        o2.pubDate
                    )
                }
                mutableStateFlow.update { stateFlow ->
                    stateFlow.copy(
                        feedItems = feedItems.reversed(),
                        podcastItems = podcastItems
                    )
                }
                Log.d(TAG, "loadItems: finish")
            }
    }

    fun updateUrl(url: String) {
        mutableStateFlow.update { it.copy(url = url) }
    }

    data class FeedViewState(
        val url: String = "https://anchor.fm/s/473e5930/podcast/rss",
        val feedItems: List<FeedItem> = ArrayList(),
        val podcastItems: List<PodcastItem> = ArrayList(),
        val isRefreshing: Boolean = false,
    )

    data class FeedItem(
        val episodeId: Long = 0,
        val imageUrl: String = "",
        val podcastTitle: String = "",
        val pubDate: String = "",
        val title: String = "",
        val description: String = "",
    )

    data class PodcastItem(
        val podcastId: Long = 0,
        val title: String = "", val imageUrl: String = ""
    )

    fun insertToHistory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Repository.insertRecord(id)
            PreferenceUtil.insertLatestId(id)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun refresh() {
        Log.d(TAG, "refresh: start refreshing")
        mutableStateFlow.update { it.copy(isRefreshing = true) }
        val refreshList = mutableListOf<Deferred<Int>>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Repository.getPodcastsList().forEach { item ->
                    refreshList.add(async(Dispatchers.IO) {
                        try {
                            Log.d(TAG, "start refresh: ${item.feedUrl}")
                            DatabaseUtil.fetchPodcast(item.feedUrl)
                            Log.d(TAG, "refresh finish: ${item.feedUrl}")
                            1
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.refresh_feed_failed))
                                }
                                mutableStateFlow.update { it.copy(isRefreshing = false) }
                                Log.e(TAG, item.title + e.toString(), e)
                                0
                            }
                        }
                    })
                }
            }
            var count = 0
            refreshList.forEach { count += it.await() }
            Log.d(TAG, "refresh: $count items")
            //Toast.makeText(context, R.string.refresh_feed_successfully, Toast.LENGTH_SHORT).show()
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.refresh_feed_successfully))
            }
            mutableStateFlow.update { it.copy(isRefreshing = false) }
        }
    }

}