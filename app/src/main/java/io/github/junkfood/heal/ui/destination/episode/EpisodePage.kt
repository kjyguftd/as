package io.github.junkfood.heal.ui.destination.episode

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.junkfood.heal.R
import io.github.junkfood.heal.database.Repository
import io.github.junkfood.heal.player.ExoPlayerHolder
import io.github.junkfood.heal.ui.common.AsyncImageImpl
import io.github.junkfood.heal.ui.common.NavigationGraph
import io.github.junkfood.heal.ui.common.NavigationGraph.toId
import io.github.junkfood.heal.ui.component.BackButton
import io.github.junkfood.heal.ui.component.HeadlineSmall
import io.github.junkfood.heal.ui.component.HtmlText
import io.github.junkfood.heal.ui.component.LabelMedium
import io.github.junkfood.heal.ui.component.SmallTopAppBar
import io.github.junkfood.heal.ui.component.SubtitleMedium
import io.github.junkfood.heal.ui.component.TitleMedium
import io.github.junkfood.heal.util.MediaUtil
import io.github.junkfood.heal.util.TextUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

private const val TAG = "EpisodePage"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun EpisodePage(
    navHostController: NavHostController,
    episodeID: Long,
    episodeViewModel: EpisodeViewModel = viewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!,
        factory = EpisodeViewModelProvider(episodeID)
    )
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewState = episodeViewModel.stateFlow.collectAsState()
    val exoPlayer = ExoPlayerHolder.get(LocalContext.current)

    LaunchedEffect(viewState.value.episodeId != episodeID)
    {
        episodeViewModel.initEpisodeContent()
    }

    with(viewState.value) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            backgroundColor = MaterialTheme.colorScheme.surface,
            topBar = {
                SmallTopAppBar(
                    title = {},
                    navigationIcon = {
                        BackButton { navHostController.popBackStack() }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.MoreVert, stringResource(R.string.more))
                        }
                    }, scrollBehavior = scrollBehavior
                )
            },
            content = {
                LazyColumn {
                    item {
                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .clickable {
                                    navHostController.navigate(
                                        NavigationGraph.PODCAST.toId(
                                            podcastId
                                        )
                                    )
                                }
                                .padding(vertical = 12.dp, horizontal = 18.dp)
                        ) {
                            AsyncImageImpl(
                                modifier = Modifier
                                    .fillMaxWidth(0.25f)
                                    .clip(MaterialTheme.shapes.small)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true),
                                model = podcastImageUrl,
                                contentDescription = null
                            )
                            Column(
                                Modifier
                                    .padding(horizontal = 18.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                TitleMedium(podcastTitle)
                                SubtitleMedium(author)
                            }

                        }

                    }
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 18.dp)
                                .padding(top = 6.dp)
                        ) {
                            HeadlineSmall(title)
                            Row(modifier = Modifier.padding(top = 3.dp)) {
                                LabelMedium(
                                    text = stringResource(R.string.publish_date).format(
                                        TextUtil.parseDate(
                                            pubDate
                                        )
                                    ),
                                    modifier = Modifier.padding(end = 9.dp)
                                )
                                LabelMedium(
                                    stringResource(R.string.duration).format(
                                        (duration / 60000f).roundToLong().toString()
                                    ),
                                    modifier = Modifier.padding(end = 18.dp)
                                )
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(top = 9.dp)
                                .padding(horizontal = 9.dp),
                            horizontalArrangement = Arrangement.End
                        ) {

                            Row(modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.padding()
                                ) {
                                    Icon(
                                        Icons.Rounded.PlaylistAdd,
                                        null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.padding()
                                ) {
                                    Icon(
                                        Icons.Rounded.DownloadForOffline,
                                        null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.padding()
                                ) {
                                    Icon(
                                        Icons.Rounded.Share,
                                        null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            FilledIconButton(
                                onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        Repository.insertRecord(episodeID)
                                    }
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val mediaSource =
                                            MediaUtil.getMediaSourceByEpisodeID(episodeID)
                                        withContext(Dispatchers.Main) {
                                            exoPlayer.stop()
                                            exoPlayer.setMediaSource(mediaSource)
                                            exoPlayer.prepare()
                                            exoPlayer.play()
                                        }
                                    }
                                },
                                modifier = Modifier.padding(end = 9.dp)
                            ) { Icon(Icons.Rounded.PlayArrow, null) }

                        }
                    }
                    item {
                        Column(
                            Modifier
                                .padding(horizontal = 18.dp)
                        ) {

                            Text(
                                text = stringResource(R.string.episode_description),
                                modifier = Modifier
                                    .padding(top = 18.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            HtmlText(
                                modifier = Modifier.padding(top = 9.dp),
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = (MaterialTheme.typography.bodyMedium.lineHeight.value + 2f).sp,
                                onTimestampClick = { timeStamp: Long ->
                                    if (ExoPlayerHolder.playerStateFlow.value.currentMediaItem.mediaId.toLong() != episodeID)
                                        return@HtmlText
                                    exoPlayer.seekTo(timeStamp)
                                }
                            )

                        }
                    }
                    item {
                        AsyncImageImpl(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(18.dp)
                                .clip(MaterialTheme.shapes.large)
                                .aspectRatio(1f),
                            model = imageUrl,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            onError = { it.result.throwable.printStackTrace() }
                        )
                    }

                }
            }
        )
    }

}
