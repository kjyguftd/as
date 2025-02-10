package io.github.junkfood.heal.database.model

import androidx.room.PrimaryKey
import java.sql.Timestamp
//holds data and provides useful methods automatically
data class PlayListItem (
    @PrimaryKey(autoGenerate = true) val playListItem: Long,
    val episodeOwnerId: Long,
    val progress: Double,
    val timestamp: Timestamp
)