package com.home.reader.ui.catalogue.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.home.reader.api.dto.IssueDto
import com.home.reader.ui.reader.configuration.ReaderConfig
import com.home.reader.utils.Constants.Sizes.COVER_HEIGHT
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogueIssueItem(
    item: IssueDto,
    seriesName: String,
    coverUrl: String,
    downloadProgress: Float?,
    onClick: (config: ReaderConfig) -> Unit,
    onDownloadClick: () -> Unit,
    cached: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .width(200.dp)
            .combinedClickable(
                onClick = {
                    val config = ReaderConfig(
                        externalId = item.id,
                        currentPage = item.currentPage,
                        lastPage = item.pagesCount - 1
                    )
                    onClick(config)
                }
            )

    ) {

        Row {
            AsyncImage(
                model = coverUrl,
                contentDescription = "Cover",
                modifier = Modifier
                    .width(COVER_WIDTH)
                    .height(COVER_HEIGHT)
            )

            Column(modifier = Modifier.padding(top = 10.dp)) {
                if (cached) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    ) {
                        Icon(Icons.Rounded.Check, "Issue cached")
                    }
                }

                if (downloadProgress == null && !cached) {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    ) {
                        Icon(Icons.Rounded.AddCircle, "Cache issue")
                    }
                } else if (downloadProgress != null && !cached) {
                    CircularProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.offset(y = (-5).dp)) {
            LinearProgressIndicator(
                progress = { item.currentPage / (item.pagesCount - 1).toFloat() },
                modifier = Modifier.width(110.dp),
            )

            Text(text = "$seriesName #${item.number}", fontSize = 12.sp, lineHeight = 14.sp)
        }
    }
}