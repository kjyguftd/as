package io.github.junkfood.heal.ui.destination.subscription

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.common.AsyncImageImpl
import io.github.junkfood.heal.ui.common.LocalNavHostController
import io.github.junkfood.heal.ui.common.NavigationGraph
import io.github.junkfood.heal.ui.common.NavigationGraph.toId
import io.github.junkfood.heal.ui.component.BackButton
import io.github.junkfood.heal.ui.component.LargeTopAppBar
import io.github.junkfood.heal.util.DatabaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SubscriptionPage(
    navController: NavController = LocalNavHostController.current,
    subscriptionViewModel: SubscriptionViewModel = viewModel()
) {
    val viewState = subscriptionViewModel.subscriptionFlow.collectAsState(ArrayList()).value
    val url = subscriptionViewModel.urlState.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                context.contentResolver.openInputStream(uri)
                    ?.let { inputStream ->
                        DatabaseUtil.parseOpmlToUrls(inputStream)
                            .forEach { scope.launch(Dispatchers.IO) { DatabaseUtil.fetchPodcast(it) } }
                    }
            }
        }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.subscribe_rss)) },
            text = {
                TextField(
                    value = url.url,
                    onValueChange = { subscriptionViewModel.updateUrl(it) }, trailingIcon = {
                        IconButton(
                            onClick = {
                                clipboardManager.getText()?.text?.let {
                                    subscriptionViewModel.updateUrl(it)
                                }
                            }) { Icon(Icons.Rounded.ContentPaste, null) }
                    })
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            DatabaseUtil.fetchPodcast(url.url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    showDialog = false
                }) {
                    Text(stringResource(R.string.fetch_podcast))
                }
                TextButton(onClick = {
                    launcher.launch("*/*")
                }) {
                    Text(stringResource(R.string.import_from_ompl))
                }
            }
        )
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(
            title = { Text(stringResource(id = R.string.subscriptions)) },
            scrollBehavior = scrollBehavior,
            navigationIcon = { BackButton { navController.popBackStack() } },
            actions = {
                IconButton(
                    onClick = { showDialog = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                }
            },
        )
    }, content = {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 18.dp)
                .padding(top = 6.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(viewState) { _, item ->
                SubscriptionCard(
                    imageModel = item.podcast.coverUrl,
                    title = item.podcast.title,
                    episodeCount = item.episodes.size
                ) { navController.navigate(NavigationGraph.PODCAST.toId(item.podcast.id)) }
            }
        }
    }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionCard(imageModel: String, title: String, episodeCount: Int, onClick: () -> Unit) {
    var s by remember { mutableStateOf(title) }
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 6.dp), onClick = onClick
    ) {
        AsyncImageImpl(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .aspectRatio(1f, matchHeightConstraintsFirst = true),
            model = imageModel,
            contentDescription = null
        )
        Text(
            s,
            modifier = Modifier.padding(
                top = 9.dp,
                bottom = 3.dp,
                start = 12.dp,
                end = 12.dp
            ),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if ((textLayoutResult.lineCount) < 2) {
                    s = "$title\n "
                }
            },
        )
        Text(
            stringResource(
                R.string.episodes
            ).format(episodeCount),
            modifier = Modifier.padding(bottom = 15.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}