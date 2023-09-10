package com.home.reader.ui.series.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.series.component.SeriesItem
import com.home.reader.ui.series.viewmodel.SeriesViewModel
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToIssuesScreen: (id: Long) -> Unit
) {

    val state by remember { viewModel.state }

    viewModel.refresh()

    if (viewModel.isOffline()) {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Offline mode",
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.DarkGray)
            )
        }
    }

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

    LazyVerticalGrid(columns = GridCells.Adaptive(COVER_WIDTH)) {
        this.items(state) {
            SeriesItem(
                series = it,
                coverRequest = viewModel.getCover(it.cover),
                onClick = onNavigateToIssuesScreen
            )
        }
    }
}