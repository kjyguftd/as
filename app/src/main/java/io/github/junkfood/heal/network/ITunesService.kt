package io.github.junkfood.heal.network

import io.github.junkfood.heal.network.model.SearchResponse
import io.github.junkfood.heal.ui.common.userAgentHeader
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ITunesService {
    @GET("/search?media=podcast")
    fun getPodcastsByTerm(
        @Query("term") term: String,
        @Query("country") country: String
    ): Call<SearchResponse>
}