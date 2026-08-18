package com.home.reader.ui.common.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.home.reader.utils.CustomFonts
import com.home.reader.utils.RecognizedData

data class RelativeRect(
    val x: Dp,
    val y: Dp,
    val width: Dp,
    val height: Dp
)

@Composable
fun RecognizedText(data: RecognizedData, baseOffsetX: Dp, height: Dp) {
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

    val measureResult = textMeasurer.measure(
        text = data.text,
        style = TextStyle(
            color = Color.Black,
            fontSize = baseFontSize.sp,
            fontFamily = CustomFonts.ComicSans,
            fontWeight = FontWeight.Normal
        ),
        maxLines = data.lines
    )

    val fontSize = ((data.rect.width() * baseFontSize) / measureResult.size.width).sp
    val offsetX = if (relativeBounds.x - baseOffsetX >= 0.dp) relativeBounds.x - baseOffsetX else 0.dp
    Text(
        text = data.text,
        lineHeight = fontSize,
        fontSize = fontSize,
        color = Color.Transparent,
        fontFamily = CustomFonts.ComicSans,
        fontWeight = FontWeight.Normal,
        softWrap = false,
        modifier = Modifier.height(height).offset(offsetX, (-2).dp)
    )

}