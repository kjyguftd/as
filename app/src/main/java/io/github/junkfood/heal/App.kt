package io.github.junkfood.heal

import android.app.Application
import android.content.Context
import android.content.Intent
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.tencent.mmkv.MMKV
import io.github.junkfood.heal.player.PodcastService
import io.github.junkfood.heal.player.PodcastServiceConnection
import io.github.junkfood.heal.util.SafeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application(), ImageLoaderFactory {

    private val podcastServiceConnection = PodcastServiceConnection()
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        applicationScope = SafeCoroutineScope(SupervisorJob())
        context = this.applicationContext
        startPodcastService()
    }

    private fun startPodcastService() {
        val intent = Intent(this, PodcastService::class.java)
        startService(intent)
        bindService(intent, podcastServiceConnection, BIND_AUTO_CREATE)
    }

    companion object {
        lateinit var applicationScope: CoroutineScope

        @SuppressWarnings("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext).diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
            .build()
    }
}