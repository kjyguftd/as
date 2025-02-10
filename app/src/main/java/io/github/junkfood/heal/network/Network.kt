package io.github.junkfood.heal.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.net.HttpURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {
    private const val TAG = "Network"
    
    private val iTunesService = ServiceCreator.create(ITunesService::class.java)



    suspend fun searchPodcastByTerm(term: String, country: String = "US") =
        iTunesService.getPodcastsByTerm(term, country).await()

    private suspend fun <T> Call<T>.await() = suspendCoroutine<T> { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                val statusCode = response.code()
                Log.d(TAG, "onResponse: ${statusCode}")

                if (body != null && statusCode == HttpURLConnection.HTTP_OK) {
                    continuation.resume(body)
                } else {
                    continuation.resumeWithException(RuntimeException("Network request failed, the HTTP status code is $statusCode"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
                t.printStackTrace()
            }
        })
    }
}