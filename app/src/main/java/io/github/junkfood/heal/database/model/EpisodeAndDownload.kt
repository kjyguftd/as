package io.github.junkfood.heal.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class EpisodeAndDownload(
    @Embedded val download: Download,
    @Relation(
        parentColumn = "episodeOwnerId",
        entityColumn = "downloadId"
    )
    val episode: Episode
)