package io.github.junkfood.heal.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.common.AsyncImageImpl
import io.github.junkfood.heal.util.TextUtil
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun SearchItem(
    imageModel: String,
    podcastTitle: String,
    onClick: () -> Unit,
    episodeDate: Date? = null,
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
                    podcastTitle,
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
                        if (dayCount <= 1L) stringResource(R.string.today) else if (dayCount <= 14L) stringResource(
                            R.string.days_before
                        ).format(dayCount.toInt())
                        else TextUtil.parseDate(episodeDate)
                    SubtitleSmall(text)
                }
            }

        }
    }
}