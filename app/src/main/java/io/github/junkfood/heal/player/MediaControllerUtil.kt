package io.github.junkfood.heal.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

object MediaControllerUtil {
    private const val TAG = "MediaControllerUtil"

    private var controllerFuture: ListenableFuture<MediaController>? = null


    val controller: MediaController?
        get() = if (controllerFuture!!.isDone) controllerFuture!!.get() else null

    fun get(context: Context): ListenableFuture<MediaController> {
        if (controllerFuture == null) {
            createControllerFuture(context)
        }
        return controllerFuture!!
    }

    private fun createControllerFuture(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PodcastService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture!!.addListener(
            {
                try {
                    controllerFuture!!.get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "Exception in createControllerFuture")
                }
            },
            MoreExecutors.directExecutor()
        )
    }


    fun release() {
        controllerFuture!!.let { MediaController.releaseFuture(it) }
    }


}