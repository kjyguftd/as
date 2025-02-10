package io.github.junkfood.heal.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class PodcastWithEpisodes(
    @Embedded val podcast: Podcast,
    @Relation(
        parentColumn = "id",
        entityColumn = "podcastId"
    )
    val episodes: List<Episode>
)