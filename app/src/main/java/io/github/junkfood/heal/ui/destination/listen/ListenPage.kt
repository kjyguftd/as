package io.github.junkfood.heal.ui.destination.listen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Forward30
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.junkfood.heal.player.ExoPlayerHolder
import io.github.junkfood.heal.ui.common.AsyncImageImpl
import io.github.junkfood.heal.ui.common.LocalNavHostController
import io.github.junkfood.heal.ui.common.NavigationGraph
import io.github.junkfood.heal.ui.common.NavigationGraph.toId
import io.github.junkfood.heal.util.TextUtil
import kotlinx.coroutines.launch

private const val TAG = "ListenPage"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ListenPage(
    navHostController: NavHostController = LocalNavHostController.current,
    onDismiss: () -> Unit = {},
    listenViewModel: ListenViewModel = viewModel()
) {
    val playerState = ExoPlayerHolder.playerStateFlow.collectAsState()
    val viewState = playerState.value
    val navigateToEpisodePage: (Long) -> Unit = { episodeId ->
        onDismiss()
        navHostController.navigate(
            NavigationGraph.EPISODE.toId(episodeId)
        )
    }
    var isDragging by remember { mutableStateOf(false) }
    var slider by remember(isDragging) {
        isDragging = false
        mutableStateOf(viewState.currentProgress)
    }
    val sliderPosition =
        remember(viewState.currentProgress) {
            derivedStateOf {
                if (isDragging) slider else viewState.currentProgress
            }
        }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier
            .padding()
            .fillMaxSize(), topBar = {
            io.github.junkfood.heal.ui.component.SmallTopAppBar(navigationIcon = {
                IconButton(
                    onClick = { onDismiss() }) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = null
                    )
                }
            }, actions = {
                IconButton(
                    onClick = {}) {
                    Icon(Icons.Outlined.MoreVert, null)
                }
            })
        }, content = { paddingValues ->
            viewState.run {
                val padding = animateDpAsState(
                    targetValue = if (isPlaying) 24.dp else 48.dp
                )
                Column(modifier = Modifier.padding(paddingValues)) {
                    Column(
                        modifier = Modifier.weight(5f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImageImpl(
                            modifier = Modifier
                                .aspectRatio(1f, true)
                                .padding(padding.value)
                                .clip(MaterialTheme.shapes.large)
                                .clickable { navigateToEpisodePage(currentEpisode.id) },
                            model = currentEpisode.cover,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .clip(MaterialTheme.shapes.large)
                                .clickable {
                                    navigateToEpisodePage(currentEpisode.id)
                                }
                                .padding(12.dp, 6.dp),
                            text = currentEpisode.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = currentPodcast.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .clickable {
                                    onDismiss()
                                    navHostController.navigate(
                                        NavigationGraph.PODCAST.toId(currentPodcast.id)
                                    )
                                }
                                .padding(12.dp, 6.dp)
                        )
                        Slider(
                            value = sliderPosition.value,
                            onValueChange = {
                                isDragging = true
                                slider = it
                            },
                            modifier = Modifier.padding(top = 12.dp),
                            onValueChangeFinished = {
                                scope.launch {
                                    listenViewModel.seekToProgress(slider)
                                }
                            }
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                TextUtil.durationToText(
                                    (currentEpisode.duration * currentProgress).toLong()
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                            )
                            Text(
                                TextUtil.durationToText(currentEpisode.duration),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                        val speedList = listOf(1.0f, 1.2f, 1.5f, 0.8f)
                        var speedIndex by rememberSaveable { mutableStateOf(0) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = {
                                if (speedIndex == 3) speedIndex = 0 else speedIndex += 1
                                listenViewModel.setPlayBackSpeed(speedList[speedIndex])
                            }) {
                                Text(
                                    text = ("${speedList[speedIndex]}x"),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = { listenViewModel.replay() }) {
                                Icon(Icons.Outlined.Replay10, null, modifier = Modifier.size(28.dp))
                            }
                            FilledIconButton(modifier = Modifier.size(54.dp), onClick = {
                                listenViewModel.playOrPause()
                            }) {
                                AnimatedVisibility(
                                    visible = isBuffering,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                                AnimatedVisibility(
                                    visible = !isBuffering,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Icon(
                                        if (isPlaying || isBuffering) Icons.Outlined.Pause else Icons.Filled.PlayArrow,
                                        null,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = { listenViewModel.forward() }) {
                                Icon(
                                    Icons.Outlined.Forward30,
                                    null,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Filled.DarkMode, null)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
//            NavigationBarImpl()
        }
    )
}