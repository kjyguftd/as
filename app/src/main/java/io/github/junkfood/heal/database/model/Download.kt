package io.github.junkfood.heal.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Download(
    @PrimaryKey(autoGenerate = true) val downloadId: Long,
    val episodeOwnerId: Long,
    val progress: Double,
    val filePath: String?
)
