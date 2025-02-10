package io.github.junkfood.heal.database

import androidx.room.Query
import androidx.room.Room
import com.icosillion.podengine.models.Podcast
import io.github.junkfood.heal.App.Companion.applicationScope
import io.github.junkfood.heal.App.Companion.context
import io.github.junkfood.heal.database.model.Episode
import io.github.junkfood.heal.database.model.EpisodeAndRecord
import io.github.junkfood.heal.database.model.Record
import io.github.junkfood.heal.network.ITunesService
import io.github.junkfood.heal.util.DatabaseUtil.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Repository {
    private const val TAG = "Repository"
    //create a Room database instance
    private val db = Room.databaseBuilder(
        context, AppDatabase::class.java, "database"
    ).build()
    //instances of DAO(data access objects)
    //DAOs are interface that define methods for accessing the database
    //each DAO corresponds to a specific table
    private val episodeDao = db.episodeDao()
    private val podcastDao = db.podcastDao()
    private val recordDao = db.recordDao()

    fun unsubscribePodcastById(Id: Long) {
        applicationScope.launch(Dispatchers.IO) {
            recordDao.getRecord().forEach {
                if (episodeDao.getEpisodeById(it.episodeId).podcastId == Id) recordDao.deleterecord(
                    it
                )
            }
            podcastDao.deletePodcastById(Id)
            episodeDao.deleteAllEpisodesByPodcastId(Id)
        }
    }

    fun getRecord() = recordDao.getRecord()

    fun deleteRecord(record: Record) = recordDao.deleterecord(record)

    fun getPodcastsWithEpisodes() = podcastDao.getPodcastsWithEpisodes()

    fun getPodcasts() = podcastDao.getAllPodcasts()

    fun getEpisodeAndRecord() = recordDao.getEpisodeAndRecordFlow()

    suspend fun deleteAllRecords() = recordDao.deleteAllRecords()

    suspend fun getEpisodeById(Id: Long) = episodeDao.getEpisodeById(Id)

    fun getEpisodesByPodcastId(podcastId: Long) = episodeDao.getEpisodesByPodcastId(podcastId)

    fun getLatestRecord(): Flow<EpisodeAndRecord> {
        return recordDao.getEpisodeAndRecordFlow().mapNotNull {
            it.last()
        }
    }

    fun updateEpisode(episode: Episode) = episodeDao.update(episode)

    suspend fun getPodcastById(Id: Long) = podcastDao.getPodcastById(Id)

    fun deleteAllEpisodesByPodcastId(Id: Long) = episodeDao.deleteAllEpisodesByPodcastId(Id)

    fun deletePodcastById(Id: Long) = podcastDao.deletePodcastById(Id)

    fun getPodcastFlowById(Id: Long) = podcastDao.getPodcastFlowById(Id)

    suspend fun importRssData(podcast: Podcast) {
        val podcastData = podcastDao.getPodcastByFeedUrl(podcast.feedURL.toExternalForm())
        val podcastId = podcastData?.id ?: podcastDao.insert(podcast.toEntity())

        for (episode in podcast.episodes) {
            val foundEpisode =
                episodeDao.getEpisodeByAudioUrl(episode.enclosure.url.toExternalForm())
            if (foundEpisode == null) {
                episodeDao.insert(
                    episode.toEntity(
                        podcastId = podcastId,
                        podcastImageUrl = podcast.imageURL.toExternalForm(),
                        podcastTitle = podcast.title
                    )
                )
            }
        }
    }

    fun insertPodcast(podcast: io.github.junkfood.heal.database.model.Podcast) =
        podcastDao.insert(podcast = podcast)

    suspend fun insertRecord(id: Long) {
        recordDao.deleteRecordById(id)
        recordDao.insertRecord(Record(episodeId = id))
    }

    suspend fun getPodcastsList() = podcastDao.getPodcastsList()

}