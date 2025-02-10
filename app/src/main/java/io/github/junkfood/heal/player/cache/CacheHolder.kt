package io.github.junkfood.heal.player.cache

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache

@UnstableApi object CacheHolder {
    private var cache: SimpleCache? = null
    private val lock = Object()

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun get(context: Context): SimpleCache {
        synchronized(lock) {
            if (cache == null) {
                val cacheSize = 200L * 1024 * 1024 // 200MB
                val exoDatabaseProvider = StandaloneDatabaseProvider(context)

                cache = SimpleCache(
                    context.cacheDir.resolve("audio_tmp"),
                    LeastRecentlyUsedCacheEvictor(cacheSize),
                    exoDatabaseProvider
                )
            }
        }
        return cache!!
    }
}