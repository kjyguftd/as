package io.github.junkfood.heal.util

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import io.github.junkfood.heal.App
import io.github.junkfood.heal.database.Repository
import io.github.junkfood.heal.database.model.Episode
import io.github.junkfood.heal.player.cache.DataSourceHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaUtil {

    @OptIn(UnstableApi::class)
    fun MediaItem.toMediaSource(): MediaSource {
        val dataSourceFactory = DataSourceHolder.getCacheFactory(App.context)
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(this)
    }

    fun Episode.toMediaItem(): MediaItem {
        val mediaMetaData = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(author)
            .setArtworkUri(Uri.parse(cover))
            .build()
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setMediaMetadata(mediaMetaData)
            .setUri(audioUrl)
            .build()
    }

    fun Episode.toMediaSource(): MediaSource {
        return toMediaItem().toMediaSource()
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    suspend fun getMediaSourceByEpisodeID(episodeID: Long): MediaSource =
        withContext(Dispatchers.IO) {
            val episode = Repository.getEpisodeById(episodeID)
            val mediaMetaData = with(episode) {
                MediaMetadata.Builder().setTitle(title).setArtist(author)
                    .setArtworkUri(Uri.parse(cover)).build()
            }
            val mediaItem =
                MediaItem.Builder().setMediaId(episodeID.toString()).setMediaMetadata(mediaMetaData)
                    .setUri(episode.audioUrl).build()
            val dataSourceFactory = DataSourceHolder.getCacheFactory(App.context)
            return@withContext ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
        }
}