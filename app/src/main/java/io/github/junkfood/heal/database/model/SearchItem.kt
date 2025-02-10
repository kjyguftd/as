package io.github.junkfood.heal.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchItem(
    @PrimaryKey(autoGenerate = true)
    val searchId: Long,
    val value: String
)