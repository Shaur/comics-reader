package com.home.reader.ui.series.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.home.reader.component.dto.SeriesDto
import com.home.reader.utils.Constants.Sizes.COVER_HEIGHT
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeriesItem(
    series: SeriesDto,
    onClick: (id: Long, name: String) -> Unit,
    onLongClick: (id: Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .combinedClickable(
                onClick = { onClick(series.id, series.name) },
                onLongClick = { onLongClick(series.id) }
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = File(series.coverPath)),
            contentDescription = "Cover",
            modifier = Modifier
                .width(COVER_WIDTH)
                .height(COVER_HEIGHT)
        )
        Text(
            text = series.name,
            fontSize = 14.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(COVER_WIDTH)
        )
        Text(
            text = "${series.completedIssues}/${series.issuesCount}",
            fontSize = 12.sp,
            lineHeight = 13.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeriesItem() {
    MaterialTheme {
        val series = SeriesDto(
            id = 1,
            name = "Good, bad and ugly: Part II",
            issuesCount = 2,
            completedIssues = 1,
            coverPath = ""
        )

        SeriesItem(series, onClick = {_, _ -> }) {}
    }
}