package io.github.junkfood.heal.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.common.AsyncImageImpl

@Composable
@Preview
fun NowPlayingBar(
    episodeTitle: String = stringResource(R.string.episode_title_sample),
    podcastTitle: String = stringResource(R.string.podcast_title_sample),
    image: String = "",
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit = {},
    onButtonClicked: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ) {
        Column {
            Row(modifier = Modifier.height(55.dp).clickable { onClick() }) {
                AsyncImageImpl(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(MaterialTheme.shapes.small),
                    model = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                        .weight(1f),
                ) {
                    Text(
                        text = episodeTitle,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(),
                        text = podcastTitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
                    onClick = { onButtonClicked() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null
                    )
                }
            }
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                progress = progress
            )
        }
    }
}