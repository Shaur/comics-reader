package com.home.reader.ui.catalogue.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.home.reader.api.dto.Issue
import com.home.reader.ui.reader.state.ReaderState
import com.home.reader.utils.Constants.Sizes.COVER_HEIGHT
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogueIssueItem(
    item: Issue,
    seriesName: String,
    coverUrl: String,
    onClick: (id: Long, currentPage: Int, lastPage: Int, mode: ReaderState.ReaderMode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .width(110.dp)
            .combinedClickable(
                onClick = { onClick(item.id, item.currentPage, item.pagesCount - 1, ReaderState.ReaderMode.REMOTE) }
            )

    ) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Cover",
            modifier = Modifier
                .width(COVER_WIDTH)
                .height(COVER_HEIGHT)
        )

        LinearProgressIndicator(
            progress = { item.currentPage / (item.pagesCount - 1).toFloat() },
            modifier = Modifier
                .offset(y = (-4).dp)
                .width(110.dp),
        )

        Text(text = "$seriesName #${item.number}", fontSize = 12.sp, lineHeight = 14.sp)
    }
}