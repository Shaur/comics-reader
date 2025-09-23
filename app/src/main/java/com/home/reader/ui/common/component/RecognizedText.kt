package com.home.reader.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.home.reader.utils.RecognizedData

private data class RelativeRect(
    val x: Dp,
    val y: Dp,
    val width: Dp,
    val height: Dp
)

@Composable
fun RecognizedText(data: RecognizedData) {
    val ctx = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val baseFontSize = 6

    val relativeBounds = with(LocalDensity.current) {
        RelativeRect(
            x = data.rect.left.toDp(),
            y = data.rect.top.toDp(),
            width = data.rect.width().toDp(),
            height = data.rect.height().toDp()
        )
    }

    CompositionLocalProvider(
        LocalTextToolbar provides SelectedTextToolbar(ctx, LocalClipboard.current)
    ) {
        SelectionContainer(
            modifier = Modifier
                .width(relativeBounds.width)
                .height(relativeBounds.height)
                .offset(relativeBounds.x, relativeBounds.y)
                .background(color = Color.Transparent)
        ) {
            val measureResult = textMeasurer.measure(
                text = data.text,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = baseFontSize.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                maxLines = data.lines
            )

            val fontSize = ((data.rect.width() * baseFontSize) / measureResult.size.width).sp
            Text(
                text = data.text,
                lineHeight = fontSize,
                fontSize = fontSize,
                color = Color.Transparent,
                fontFamily = FontFamily.SansSerif,
                softWrap = false
            )
        }
    }

}