package io.github.junkfood.heal.ui.destination.feed

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.junkfood.heal.App
import io.github.junkfood.heal.R
import io.github.junkfood.heal.player.ExoPlayerHolder
import io.github.junkfood.heal.ui.common.NavigationGraph
import io.github.junkfood.heal.ui.common.NavigationGraph.toId
import io.github.junkfood.heal.ui.common.SnackbarUtil.snackbarHostState
import io.github.junkfood.heal.ui.component.FeedItem
import io.github.junkfood.heal.ui.component.HistoryCard
import io.github.junkfood.heal.util.MediaUtil.getMediaSourceByEpisodeID
import io.github.junkfood.heal.util.TextUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun FeedPage(navHostController: NavHostController, feedViewModel: FeedViewModel = viewModel()) {
    val viewState = feedViewModel.stateFlow.collectAsState()
    val libraryDataState = feedViewModel.episodeAndRecordFlow.collectAsState(ArrayList())
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val exoPlayer = ExoPlayerHolder.get(LocalContext.current)
    val padding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    viewState.value.run {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                feedViewModel.refresh()
            }, indicatorPadding = PaddingValues(top = padding)
        ) {
            Scaffold(
                modifier = Modifier
                    .padding()
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    io.github.junkfood.heal.ui.component.SmallTopAppBar(title = {}, actions = {
                        IconButton(onClick = { navHostController.navigate(NavigationGraph.SETTINGS) }) {
                            Icon(Icons.Outlined.Settings, null)
                        }
                    },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = {
//                    NavigationBarImpl()
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) {

                LazyColumn(modifier = Modifier.padding(it)) {
                    item {
                        Column {
                            Text(
                                stringResource(R.string.resume_listening),
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 12.dp
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item { Spacer(modifier = Modifier.size(0.dp)) }
                                val episodeList = libraryDataState.value.reversed()
                                for (item in episodeList) {
                                    item {
                                        HistoryCard(
                                            imageModel = item.episode.cover,
                                            title = item.episode.title,
                                            timeLeft = stringResource(R.string.minutes_left).format(
                                                ((1f - item.episode.progress) * (item.episode.duration / 60000L)).toInt()
                                            ),
                                            /*length = item.episode.duration,
                                            progress = item.episode.progress,*/
                                            onClick = {
                                                navHostController.navigate(
                                                    NavigationGraph.EPISODE.toId(
                                                        item.episode.id
                                                    )
                                                )
//                                                libraryViewModel.insertToHistory(item.episode.id)
                                            }
                                        )
                                    }
                                }
                                item { Spacer(modifier = Modifier.size(0.dp)) }
                            }

                        }
                    }
                    for (item in feedItems) {
                        item {
                            FeedItem(
                                imageModel = item.imageUrl,
                                title = item.podcastTitle,
                                episodeTitle = item.title,
                                episodeDescription = item.description,
                                onClick = {
//                                        feedViewModel.jumpToEpisode(i)
//                                        navHostController.navigate(RouteName.EPISODE)
                                    navHostController.navigate(
                                        NavigationGraph.EPISODE.toId(
                                            item.episodeId
                                        )
                                    )
                                },
                                episodeDate = TextUtil.parseXmlStringToDate(item.pubDate),
                                onAddButtonClick = {},
                                onDownloadButtonClick = {},
                                onMoreButtonClick = {},
                                onPlayButtonClick = {
                                    feedViewModel.insertToHistory(item.episodeId)
                                    App.applicationScope.launch(Dispatchers.IO) {
                                        val mediaSource = getMediaSourceByEpisodeID(item.episodeId)
                                        withContext(Dispatchers.Main) {
                                            exoPlayer.stop()
                                            exoPlayer.setMediaSource(mediaSource)
                                            exoPlayer.prepare()
                                            exoPlayer.play()

                                        }
                                    }
                                }
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5f.dp)
                                    .clip(MaterialTheme.shapes.extraLarge),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )

                        }
                    }
                }

            }

        }
    }
}


