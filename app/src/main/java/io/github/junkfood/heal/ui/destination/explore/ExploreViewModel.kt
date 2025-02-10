package io.github.junkfood.heal.ui.destination.explore

import android.app.appsearch.AppSearchResult
import androidx.lifecycle.ViewModel
import io.github.junkfood.heal.database.model.Podcast
import io.github.junkfood.heal.database.model.SearchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


class ExploreViewModel : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(ExploreViewState())
    val stateFlow = mutableStateFlow.asStateFlow()

    data class ExploreViewState(
        val searchHistory: List<SearchItem> = ArrayList(),
        val suggestions: List<Podcast> = ArrayList(),
    )

    init {
        ///todo
    }


}