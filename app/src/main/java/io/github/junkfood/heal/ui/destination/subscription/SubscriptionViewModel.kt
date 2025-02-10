package io.github.junkfood.heal.ui.destination.subscription

import androidx.lifecycle.ViewModel
import io.github.junkfood.heal.database.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update

class SubscriptionViewModel : ViewModel() {
    val subscriptionFlow = Repository.getPodcastsWithEpisodes().filterNotNull()

    data class ViewState(val url: String = "https://anchor.fm/s/473e5930/podcast/rss")

    private val mutableStateFlow = MutableStateFlow(ViewState())
    val urlState = mutableStateFlow.asStateFlow()
    fun updateUrl(url: String) {
        mutableStateFlow.update { it.copy(url = url) }
    }


}