package com.home.reader.ui.issues.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.home.reader.ui.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.ui.issues.component.IssueItem
import com.home.reader.ui.issues.viewmodel.IssuesViewModel
import com.home.reader.utils.Constants

@Composable
fun IssuesScreen(
    viewModel: IssuesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    seriesId: Long,
    onNavigateToReaderScreen: (id: Long, currentPage: Int, lastPage: Int) -> Unit
) {
    val state by remember { viewModel.state }

    viewModel.refresh(seriesId)

    if (state.isEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
        }
    }

    LazyVerticalGrid(columns = GridCells.Adaptive(Constants.Sizes.COVER_WIDTH)) {
        this.items(state) {
            IssueItem(
                issue = it,
                coverRequest = viewModel.getCover(it.id),
                onClick = onNavigateToReaderScreen
            )
        }
    }
}