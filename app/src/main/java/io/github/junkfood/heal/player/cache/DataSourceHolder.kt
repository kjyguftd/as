package io.github.junkfood.heal.player.cache

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource


@UnstableApi object DataSourceHolder {
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null
    private var defaultDataSourceFactory: DataSource.Factory? = null

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getCacheFactory(context: Context): CacheDataSource.Factory {
        if (cacheDataSourceFactory == null) {
            val simpleCache = CacheHolder.get(context)
            val defaultFactory = getDefaultFactory(context)
            cacheDataSourceFactory = CacheDataSource.Factory().apply {
                setCache(simpleCache)
                // Set the URI protocol related parameters to read from the cache
                setUpstreamDataSourceFactory(defaultFactory)
                // Set the CacheDataSource factory type to read the cache
                setCacheReadDataSourceFactory(FileDataSource.Factory())
                // Write cache settings
                setCacheWriteDataSinkFactory(
                    CacheDataSink.Factory()
                        .setCache(simpleCache)
                        .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
                )
            }
        }
        return cacheDataSourceFactory!!
    }

    private fun getDefaultFactory(context: Context): DataSource.Factory {
        if (defaultDataSourceFactory == null) {
            defaultDataSourceFactory = DefaultDataSource.Factory(context)
        }
        return defaultDataSourceFactory!!
    }
}