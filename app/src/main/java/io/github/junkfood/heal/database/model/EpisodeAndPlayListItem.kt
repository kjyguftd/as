package io.github.junkfood.heal.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class EpisodeAndPlayListItem(
    @Embedded val playListItem: PlayListItem,
    @Relation(
        parentColumn = "episodeOwnerId",
        entityColumn = "playListItemId"
    )
    val episode: Episode
)
