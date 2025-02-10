package io.github.junkfood.heal.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.junkfood.heal.database.model.SearchItem
import kotlinx.coroutines.flow.Flow

interface SearchDao {
    @Insert
    fun insert(vararg searchItems: SearchItem)

    @Delete
    fun delete(vararg searchItem: SearchItem)

    @Query("SELECT * FROM SearchItem")
    fun getSearchHistory(): Flow<List<SearchItem>>
}