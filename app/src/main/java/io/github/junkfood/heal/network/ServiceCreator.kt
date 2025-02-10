package io.github.junkfood.heal.network

import io.github.junkfood.heal.ui.common.userAgentHeader
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val baseUrl = "https://itunes.apple.com"


    private val client = OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
        val request = chain.request()
        val build = request.newBuilder()
            .addHeader("User-agent", userAgentHeader).build()
        return@addInterceptor chain.proceed(build)
    }.retryOnConnectionFailure(true).build()

    private val retrofit =
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun<T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun<reified T> create(): T = ServiceCreator.create(T::class.java)
}