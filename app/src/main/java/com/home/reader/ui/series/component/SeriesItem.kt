package com.home.reader.ui.series.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.home.reader.R
import com.home.reader.api.dto.Series

@Composable
fun SeriesItem(
    series: Series,
    coverRequest: ImageRequest?,
    onClick: (id: Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .clickable { onClick(series.id) }
    ) {
        if (coverRequest == null) {
            Image(
                painter = painterResource(id = R.drawable.example_appwidget_preview),
                contentDescription = "Cover",
                modifier = Modifier
                    .width(110.dp)
                    .height(177.dp)
            )
        } else {
            AsyncImage(
                model = coverRequest,
                contentDescription = "Cover",
                modifier = Modifier
                    .width(110.dp)
                    .height(177.dp)
            )
        }
        Text(text = series.name, fontSize = 14.sp, lineHeight = 15.sp)
        Text(text = "${series.completedIssues}/${series.issuesCount}")
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSeriesItem() {
    MaterialTheme {
        val series = Series(
            id = 1,
            cover = 0,
            name = "Test issue",
            issuesCount = 2,
            completedIssues = 1
        )

        SeriesItem(series, null, {})
    }
}