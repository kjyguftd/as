package io.github.junkfood.heal.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.junkfood.heal.database.dao.EpisodeDao
import io.github.junkfood.heal.database.dao.PodcastDao
import io.github.junkfood.heal.database.dao.RecordDao
import io.github.junkfood.heal.database.model.Episode
import io.github.junkfood.heal.database.model.Podcast
import io.github.junkfood.heal.database.model.Record
import io.github.junkfood.heal.database.model.SearchItem

//define a Room database
@Database(
    entities = [
        Episode::class,
        Podcast::class,
        Record::class,
        SearchItem::class],
    version = 1,
    exportSchema = false)
//extend RoomDatabase
abstract class AppDatabase: RoomDatabase() {
    //abstract methods to get DAO instances for each entities
    //EpisodeDao is an interface containing methods to interact with the Episode table
    abstract fun episodeDao(): EpisodeDao
    abstract fun podcastDao(): PodcastDao
    abstract fun recordDao(): RecordDao
}