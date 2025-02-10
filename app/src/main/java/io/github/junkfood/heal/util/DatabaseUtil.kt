package io.github.junkfood.heal.util

import android.util.Log
import be.ceau.opml.OpmlParser
import be.ceau.opml.OpmlWriter
import be.ceau.opml.entity.Body
import be.ceau.opml.entity.Head
import be.ceau.opml.entity.Opml
import be.ceau.opml.entity.Outline
import com.icosillion.podengine.models.Episode
import com.icosillion.podengine.models.Podcast
import io.github.junkfood.heal.App
import io.github.junkfood.heal.App.Companion.context
import io.github.junkfood.heal.R
import io.github.junkfood.heal.database.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL

object DatabaseUtil {
    private const val TAG = "DatabaseUtil"
    suspend fun fetchPodcast(url: String) {
        try {
            val podcast = Podcast(URL(url))
            Repository.importRssData(podcast)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "fetchPodcast: $url")
        }
    }

    fun unsubscribePodcastById(Id: Long) {
        App.applicationScope.launch(Dispatchers.IO) {
            Repository.getRecord().forEach {
                if (Repository.getEpisodeById(it.episodeId).podcastId == Id) {
                    Repository.deleteRecord(it)
                }
            }
            Repository.deletePodcastById(Id)
            Repository.deleteAllEpisodesByPodcastId(Id)
        }
    }

    fun Episode.toEntity(
        podcastId: Long,
        podcastImageUrl: String,
        podcastTitle: String
    ): io.github.junkfood.heal.database.model.Episode {
        return with(this) {
            io.github.junkfood.heal.database.model.Episode(
                podcastId = podcastId,
                title = title,
                description = iTunesInfo.summary ?: description,
                cover = iTunesInfo.imageString ?: podcastImageUrl,
                pubDate = TextUtil.formatDate(pubDate),
                duration = TextUtil.parseTextToDuration(iTunesInfo.duration),
                author = author ?: podcastTitle,
                audioUrl = enclosure.url.toExternalForm()
            )
        }
    }

    fun Podcast.toEntity(): io.github.junkfood.heal.database.model.Podcast {
        return with(this) {
            io.github.junkfood.heal.database.model.Podcast(
                title = title,
                description = description,
                author = iTunesInfo.author.toString(),
                coverUrl = imageURL.toExternalForm(),
                url = link.toExternalForm(),
                feedUrl = feedURL.toExternalForm()
            )
        }
    }

    fun parseOpmlToUrls(inputStream: InputStream): List<String> {
        kotlin.runCatching {
            val opml = OpmlParser().parse(inputStream)
            val urlList = mutableListOf<String>()
            val outlines = opml.body.outlines
            outlines.forEach { outline ->
                if (outline.getAttribute("type") == "rss") {
                    urlList.add(outline.getAttribute("xmlUrl"))
                }
            }
            urlList
        }.onFailure { exception ->
            exception.printStackTrace()
        }.also {
            inputStream.close()
            return it.getOrElse { emptyList() }
        }
    }

    fun exportUrlsToOpml(podcastList: List<io.github.junkfood.heal.database.model.Podcast>): String {
        val title = context.getString(R.string.opml_title)
        val head = Head(
            title,
            TextUtil.getOpmlStyleDate(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val outlineList = podcastList.map { podcast ->
            with(podcast) {
                Outline(
                    mapOf(
                        "text" to title,
                        "title" to title,
                        "type" to "rss",
                        "xmlUrl" to feedUrl,
                        "htmlUrl" to url
                    ), emptyList()
                )
            }
        }
        val body = Body(outlineList)
        val opml = Opml("2.0", head, body)
        return OpmlWriter().write(opml)
    }
}