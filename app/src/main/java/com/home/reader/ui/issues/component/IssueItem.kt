package com.home.reader.ui.issues.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.home.reader.component.dto.IssueDto
import com.home.reader.ui.reader.configuration.ReaderConfig
import com.home.reader.utils.Constants
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IssueItem(
    issue: IssueDto,
    onClick: (config: ReaderConfig) -> Unit,
    onLongClock: (id: Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .width(110.dp)
            .combinedClickable(
                onClick = {
                    val config = ReaderConfig(
                        id = issue.id,
                        externalId = issue.externalId,
                        currentPage = issue.currentPage,
                        lastPage = issue.pagesCount - 1
                    )
                    onClick(config)
                },
                onLongClick = { onLongClock(issue.id) }
            )

    ) {
        Image(
            painter = rememberAsyncImagePainter(model = File(issue.coverPath)),
            contentDescription = "Cover",
            modifier = Modifier
                .width(Constants.Sizes.COVER_WIDTH)
                .height(Constants.Sizes.COVER_HEIGHT)
        )

        LinearProgressIndicator(
            progress = { issue.currentPage / (issue.pagesCount - 1).toFloat() },
            modifier = Modifier
                .offset(y = (-4).dp)
                .width(110.dp),
        )

        Text(text = "${issue.seriesName} #${issue.issue}", fontSize = 12.sp, lineHeight = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val dto = IssueDto(id = 1L, seriesName = "Good, bad and ugly", issue = "5")
    MaterialTheme {
        IssueItem(issue = dto, onClick = { _ -> }, onLongClock = { _ -> })
    }
}
