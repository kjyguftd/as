package io.github.junkfood.heal.ui.component

import android.graphics.Typeface
import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.getSpans
import io.github.junkfood.heal.R
import io.github.junkfood.heal.util.TextUtil

private const val TAG = "HtmlText"
lateinit var annotations: List<AnnotatedString.Range<String>>

@Composable
@Preview
fun BugSample() {
    SelectionContainer {
        Text(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }, text = stringResource(R.string.podcast_title_sample).repeat(10)
        )
    }
}

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.podcast_title_sample),
    urlSpanStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    ),
    isTimeStampEnabled: Boolean = true,
    timeStampSpanStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace
    ),
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    onTimestampClick: (timeStamp: Long) -> Unit = { },
) {
    val annotatedString =
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            .toAnnotatedString(urlSpanStyle, timeStampSpanStyle)
    annotations = annotatedString.getStringAnnotations(0, annotatedString.length)
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
//    SelectionContainer {
    Text(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { position ->
                    layoutResult.value?.let { layoutResult ->
                        val offset = layoutResult.getOffsetForPosition(position)
                        annotations.find { range -> offset >= range.start && offset <= range.end }
                            ?.let { stringRange ->
                                if (stringRange.tag == "url") { // NON-NLS
                                    uriHandler.openUri(stringRange.item)
                                } else if (stringRange.tag == "timeStamp") {
                                    onTimestampClick(TextUtil.parseTextToDuration(stringRange.item))
                                }
                            }
                    }
                }
                )
            },
        text = annotatedString,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        inlineContent = inlineContent,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        },
        style = style
    )
}


fun Spanned.toAnnotatedString(
    urlSpanStyle: SpanStyle = SpanStyle(
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    ),
    timeStampSpanStyle: SpanStyle = SpanStyle(
        color = Color.Blue, fontWeight = FontWeight.Bold
    ),
    isTimeStampEnabled: Boolean = true

): AnnotatedString {
    return buildAnnotatedString {
        val rawString = this@toAnnotatedString.toString()
        append(rawString)
        val urlSpans = getSpans<URLSpan>()
        val styleSpans = getSpans<StyleSpan>()
        val colorSpans = getSpans<ForegroundColorSpan>()
        val underlineSpans = getSpans<UnderlineSpan>()
        val strikethroughSpans = getSpans<StrikethroughSpan>()
        if (isTimeStampEnabled) {
            Regex("(\\d{1,2}:)+\\d\\d").findAll(rawString).forEach {
                addStyle(
                    timeStampSpanStyle, it.range.first,
                    it.range.last + 1
                )
                addStringAnnotation(
                    "timeStamp",
                    rawString.substring(it.range),
                    it.range.first,
                    it.range.last + 1
                )
            }
        }
        urlSpans.forEach { urlSpan ->
            val start = getSpanStart(urlSpan)
            val end = getSpanEnd(urlSpan)
            addStyle(urlSpanStyle, start, end)
            addStringAnnotation("url", urlSpan.url, start, end) // NON-NLS
        }
        /*        colorSpans.forEach { colorSpan ->
                    val start = getSpanStart(colorSpan)
                    val end = getSpanEnd(colorSpan)
                    addStyle(SpanStyle(color = Color(colorSpan.foregroundColor)), start, end)
                }*/
        styleSpans.forEach { styleSpan ->
            val start = getSpanStart(styleSpan)
            val end = getSpanEnd(styleSpan)
            when (styleSpan.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }
        }
        underlineSpans.forEach { underlineSpan ->
            val start = getSpanStart(underlineSpan)
            val end = getSpanEnd(underlineSpan)
            addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
        }
        strikethroughSpans.forEach { strikethroughSpan ->
            val start = getSpanStart(strikethroughSpan)
            val end = getSpanEnd(strikethroughSpan)
            addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
        }
    }
}

