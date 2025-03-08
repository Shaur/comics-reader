package com.home.reader.ui.catalogue.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.home.reader.api.dto.SeriesDto
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.catalogue.component.CatalogueIssueItem
import com.home.reader.ui.catalogue.component.CatalogueSeriesItem
import com.home.reader.ui.catalogue.viewmodel.CatalogueViewModel
import com.home.reader.ui.reader.configuration.ReaderConfig
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH
import androidx.compose.ui.Modifier

@Composable
fun CatalogueScreen(
    viewModel: CatalogueViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToReaderScreen: (config: ReaderConfig) -> Unit
) {

    val seriesState = viewModel.seriesState.collectAsLazyPagingItems()
    val issuesState by remember { viewModel.issuesState }
    var selectedSeries by remember { mutableStateOf<SeriesDto?>(null) }
    val downloadProgressState by remember { viewModel.downloadProgress }
    val cached by remember { viewModel.cached }

    LaunchedEffect(selectedSeries) {
        val seriesId = selectedSeries?.id
        if (seriesId != null) {
            viewModel.refreshIssuesState(seriesId)
        }
    }

    Column {
        BackHandler(enabled = (selectedSeries != null)) {
            selectedSeries = null
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(COVER_WIDTH + if (selectedSeries == null) 0.dp else 30.dp)
        ) {

            if (selectedSeries == null) {
                items(seriesState.itemCount) { index ->
                    val item = seriesState[index]
                    if (item != null) {
                        CatalogueSeriesItem(
                            item = item,
                            coverUrl = viewModel.coverRequest(item.cover, "MEDIUM"),
                            onClick = { selectedSeries = item }
                        )
                    }
                }

                if (seriesState.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

                if (seriesState.loadState.append is LoadState.Error) {
                    item {
                        Text(
                            text = "Error loading more items",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            if (selectedSeries != null) {
                items(issuesState) {
                    CatalogueIssueItem(
                        item = it,
                        seriesName = selectedSeries?.title ?: "",
                        coverUrl = viewModel.coverRequest("/pages/${it.id}/0", "MEDIUM"),
                        onClick = onNavigateToReaderScreen,
                        downloadProgress = downloadProgressState[it.id],
                        cached = (cached[it.id] == true),
                        onDownloadClick = {
                            val issueId = it.id
                            val seriesId = selectedSeries?.id ?: 0L
                            viewModel.download(seriesId, issueId)
                        }
                    )
                }
            }
        }
    }
}