package io.github.junkfood.heal.ui.destination.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.junkfood.heal.database.model.Podcast
import io.github.junkfood.heal.network.Network
import io.github.junkfood.heal.network.model.PodcastSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(SearchViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private val TAG = "SearchViewModel"

    data class SearchViewState(
        val searchString: String = "",
        val searchResult: List<Podcast> = ArrayList(),
    )

    fun updateSearchString(string: String) {
        mutableStateFlow.update { it.copy(searchString = string) }
    }

    fun searchPodcast() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(
                        TAG,
                        "searchPodcast: start searching ${mutableStateFlow.value.searchString}"
                    )
                    val response = Network.searchPodcastByTerm(mutableStateFlow.value.searchString)
                    Log.d(TAG, "searchPodcast: search succeeded")
                    val result = ArrayList<Podcast>()
                    response.results.forEach {
                        Log.d(TAG, "searchPodcast: ${it.collectionName}")
                        result.add(
                            Podcast(
                                title = it.collectionName ?: "Unknown Podcast",
                                author = it.artistName ?: "Unknown Artist",
                                feedUrl = it.feedUrl ?: "",
                                coverUrl = it.artworkUrl100 ?: ""
                            )
                        )
                    }
                    mutableStateFlow.update {
                        it.copy(searchResult = result)
                    }
                } catch (e: Exception) {
                    Result.failure<List<PodcastSearchResult>>(e)
                    Log.d(TAG, "searchPodcast: failed")
                }

            }
        }
    }
}