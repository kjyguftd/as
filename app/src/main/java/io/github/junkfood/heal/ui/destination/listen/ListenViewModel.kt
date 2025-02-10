package io.github.junkfood.heal.ui.destination.listen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import io.github.junkfood.heal.App
import io.github.junkfood.heal.player.ExoPlayerHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ListenViewModel : ViewModel() {
    private val exoPlayer = ExoPlayerHolder.get(App.context)
    private val TAG = "ListenViewModel"
    private val mutableStateFlow = MutableStateFlow(ViewState())
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ExoPlayerHolder.playerStateFlow.collect { playerState ->
                with(playerState) {
                    mutableStateFlow.update {
                        it.copy(
                            episodeId = currentEpisode.id,
                            episodeTitle = currentEpisode.title,
                            podcastTitle = currentPodcast.title,
                            podcastId = currentPodcast.id,
                            imageUrl = currentEpisode.cover,
                            duration = currentEpisode.duration,
                            progress = currentProgress,
                            isPlaying = isPlaying,
                            isBuffering = isBuffering,
                        )
                    }
                }
            }
        }
    }

    data class ViewState(
        val episodeId: Long = 0,
        val episodeTitle: String = "",
        val podcastTitle: String = "",
        val podcastId: Long = 0,
        val imageUrl: String = "",
        val duration: Long = 0,
        val progress: Float = 0F,
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false
    )

    fun forward() {
        exoPlayer.seekForward() // The ForwardIncrementMs is set in ExoPlayerHolder.kt
    }

    fun replay() {
        exoPlayer.seekBack() // The BackIncrementMs is set in ExoPlayerHolder.kt
    }

    fun setPlayBackSpeed(speed: Float) {
        exoPlayer.setPlaybackSpeed(speed)
    }

    fun seekToProgress(progress: Float) {
        exoPlayer.seekTo((exoPlayer.duration * progress).toLong())
    }


    fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

}
