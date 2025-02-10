package io.github.junkfood.heal.player

import android.content.Context
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import io.github.junkfood.heal.App.Companion.applicationScope
import io.github.junkfood.heal.App.Companion.context
import io.github.junkfood.heal.database.Repository
import io.github.junkfood.heal.database.model.Episode
import io.github.junkfood.heal.database.model.Podcast
import io.github.junkfood.heal.util.MediaUtil.toMediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExoPlayerHolder {
    private const val TAG = "ExoPlayerHolder"
    private val exoPlayer: ExoPlayer by lazy { createExoPlayer(context) }

    private const val FORWARD_INCREMENT_MS = 30000L
    private const val BACK_INCREMENT_MS = 10000L

    data class PlayerState(
        val currentMediaItem: MediaItem = MediaItem.EMPTY,
        val currentEpisode: Episode = Episode(),
        val currentPodcast: Podcast = Podcast(),
        val currentProgress: Float = 0F,
        val isPlayerAvailable: Boolean = false,
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false,
    )

    private val mutableStateFlow = MutableStateFlow(PlayerState())
    val playerStateFlow = mutableStateFlow.asStateFlow()

    @Synchronized
    fun get(context: Context): ExoPlayer {
        if (!mutableStateFlow.value.isPlayerAvailable) {
            applicationScope.launch { loadEpisodeFromDatabase() }
            applicationScope.launch { updatePlayerProgress() }
            applicationScope.launch { updateEpisodeProgress() }
            mutableStateFlow.update { it.copy(isPlayerAvailable = true) }
        }
        return exoPlayer
    }

    private suspend fun updatePlayerProgress() {
        withContext(Dispatchers.Main) {
            while (true) {
                delay(500)
                mutableStateFlow.update { it.copy(currentProgress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()) }
            }
        }
    }

    private suspend fun updateEpisodeProgress() {
        withContext(Dispatchers.IO) {
            while (true) {
                delay(5000)
                with(playerStateFlow.value) {
                    Repository.updateEpisode(currentEpisode.copy(progress = currentProgress))
                }
            }
        }
    }

    fun get(): ExoPlayer {
        return exoPlayer
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private suspend fun loadEpisodeFromDatabase() {
        Repository.getLatestRecord().filterNotNull().distinctUntilChangedBy { it.episode.id }
            .collect {
                Log.d(TAG, it.episode.toString())
                with(it) {
                    val podcast = Repository.getPodcastById(it.episode.podcastId)
                    val mediaSource = episode.toMediaSource()
                    mutableStateFlow.update { playerState ->
                        playerState.copy(
                            currentEpisode = episode,
                            currentPodcast = podcast
                        )
                    }
                    withContext(Dispatchers.Main) {
                        with(exoPlayer) {
                            if (mediaItemCount == 0 || currentMediaItem!!.mediaId != episode.id.toString()) {
                                setMediaSource(mediaSource)
                                prepare()
                                seekTo((episode.duration * episode.progress).toLong())
                            }
                        }
                    }
                }
            }
    }

    @Synchronized
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun createExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setSeekForwardIncrementMs(FORWARD_INCREMENT_MS)
            .setSeekBackIncrementMs(BACK_INCREMENT_MS)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(PlayerListener)
            }
    }

    private object PlayerListener : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mutableStateFlow.update { it.copy(currentMediaItem = mediaItem ?: MediaItem.EMPTY) }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (!playerStateFlow.value.isBuffering)
                mutableStateFlow.update {
                    it.copy(isPlaying = isPlaying)
                }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            mutableStateFlow.update {
                it.copy(
                    isBuffering = playbackState == ExoPlayer.STATE_BUFFERING
                )
            }
        }

    }

}