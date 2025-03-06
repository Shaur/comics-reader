package com.home.reader.ui.catalogue.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.home.reader.api.dto.SeriesDto
import com.home.reader.utils.Constants.Sizes.COVER_HEIGHT
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogueSeriesItem(
    item: SeriesDto,
    coverUrl: String,
    onClick: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .combinedClickable(
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Cover",
            modifier = Modifier
                .width(COVER_WIDTH)
                .height(COVER_HEIGHT)
        )
        Text(
            text = item.title,
            fontSize = 14.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(COVER_WIDTH)
        )
        Text(
            text = "${item.completedIssuesCount}/${item.issuesCount}",
            fontSize = 12.sp,
            lineHeight = 13.sp
        )
    }
}