package com.home.reader.ui.issues.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.home.reader.R
import com.home.reader.api.dto.Issue

@Composable
fun IssueItem(
    issue: Issue,
    coverRequest: ImageRequest?,
    onClick: (id: Long, currentPage: Int, lastPage: Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(3.dp)
            .clickable { onClick(issue.id, issue.currentPage, issue.pagesCount - 1) }
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

        LinearProgressIndicator(
            progress = issue.currentPage / issue.pagesCount.toFloat(),
            modifier = Modifier
                .offset(y = (-8).dp)
                .width(110.dp)
        )

        Text(text = issue.number, fontSize = 14.sp, lineHeight = 15.sp)
    }
}