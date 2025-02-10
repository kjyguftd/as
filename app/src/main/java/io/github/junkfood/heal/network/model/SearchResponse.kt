package io.github.junkfood.heal.network.model

data class SearchResponse(
    val resultCount: Int,
    val results: List<PodcastSearchResult>
)