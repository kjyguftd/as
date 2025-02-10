package io.github.junkfood.heal.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.common.AsyncImageImpl
import io.github.junkfood.heal.util.TextUtil
import io.github.junkfood.heal.util.TextUtil.trimDescription
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
@Composable
fun EpisodeItem(
    imageModel: String,
    episodeTitle: String,
    episodeDescription: String,
    onClick: () -> Unit,
    episodeDate: Date? = null
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .aspectRatio(5f, matchHeightConstraintsFirst = true)
                .height(IntrinsicSize.Min)
        ) {
            AsyncImageImpl(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true),
                model = imageModel,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 9.dp)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Top
            ) {
                Text(
                    episodeTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                episodeDate?.let {
                    val dayCount = TimeUnit.DAYS.convert(
                        (Calendar.getInstance().time.time - episodeDate.time),
                        TimeUnit.MILLISECONDS
                    )
                    val text =
                        if (dayCount == 0L) stringResource(R.string.today) else if (dayCount <= 60L) stringResource(
                            R.string.days_before
                        ).format(dayCount.toInt())
                        else TextUtil.parseDate(episodeDate)
                    SubtitleSmall(text)
                }
            }

        }
        Text(
            text = HtmlCompat.fromHtml(episodeDescription, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .toString().trimDescription(200),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 9.dp),
        )

    }
}

@Composable
fun FeedItem(
    imageModel: String,
    title: String,
    episodeTitle: String,
    episodeDescription: String,
    onClick: () -> Unit,
    episodeDate: Date? = null,
    onPlayButtonClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    onMoreButtonClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 15.dp)
                .aspectRatio(4.5f, matchHeightConstraintsFirst = true)
                .height(IntrinsicSize.Min)
        ) {
            AsyncImageImpl(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true),
                model = imageModel,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 9.dp)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Top
            ) {
                Text(
                    episodeTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    title,
                    modifier = Modifier.padding(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                episodeDate?.let {
                    val dayCount = TimeUnit.DAYS.convert(
                        (Calendar.getInstance().time.time - episodeDate.time),
                        TimeUnit.MILLISECONDS
                    )
                    val text =
                        if (dayCount <= 1L) stringResource(R.string.today) else if (dayCount <= 14L) stringResource(
                            R.string.days_before
                        ).format(dayCount.toInt())
                        else TextUtil.parseDate(episodeDate)
                    SubtitleSmall(text)
                }
            }

        }
        Text(
            text = HtmlCompat.fromHtml(episodeDescription, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .toString().trimDescription(200),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 9.dp)
                .padding(horizontal = 12.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
        ) {

            Row(modifier = Modifier.weight(1f)) {
                IconButton(
                    onClick = onAddButtonClick,
                    modifier = Modifier.padding()
                ) {
                    Icon(
                        Icons.Rounded.PlaylistAdd,
                        null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = onDownloadButtonClick,
                    modifier = Modifier.padding()
                ) {
                    Icon(
                        Icons.Outlined.DownloadForOffline,
                        null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = onMoreButtonClick,
                    modifier = Modifier.padding()
                ) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            FilledIconButton(
                onClick = onPlayButtonClick,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .size(32.dp)
            ) { Icon(Icons.Rounded.PlayArrow, null) }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCard(imageModel: String, title: String, timeLeft: String, onClick: () -> Unit) {
    var s by remember { mutableStateOf(title) }

    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .width(150.dp), onClick = onClick
    ) {
        AsyncImageImpl(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .aspectRatio(1f, matchHeightConstraintsFirst = true),
            model = imageModel,
            contentDescription = null, contentScale = ContentScale.Crop
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
            overflow = TextOverflow.Ellipsis, onTextLayout = { textLayoutResult ->
                if ((textLayoutResult.lineCount) < 2) {
                    s = "$title\n "
                }
            }
        )
        Text(
            timeLeft,
            modifier = Modifier.padding(bottom = 15.dp, start = 12.dp, end = 12.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}