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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var selectedSeriesId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedSeriesName by rememberSaveable { mutableStateOf<String?>(null) }
    val downloadProgressState by remember { viewModel.downloadProgress }
    val cached by remember { viewModel.cached }

    LaunchedEffect(selectedSeriesId) {
        selectedSeriesId?.let { viewModel.refreshIssuesState(it) }
        if (selectedSeriesId == null) seriesState.refresh()
    }

    Column {
        BackHandler(enabled = (selectedSeriesId != null)) {
            selectedSeriesId = null
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(COVER_WIDTH + if (selectedSeriesId == null) 0.dp else 30.dp)
        ) {

            if (selectedSeriesId == null) {
                items(seriesState.itemCount) { index ->
                    val item = seriesState[index]
                    if (item != null) {
                        CatalogueSeriesItem(
                            item = item,
                            coverUrl = viewModel.coverRequest(item.cover, "SMALL"),
                            onClick = {
                                selectedSeriesId = item.id
                                selectedSeriesName = item.title
                            }
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

            if (selectedSeriesId != null) {
                items(issuesState) {
                    CatalogueIssueItem(
                        item = it,
                        seriesName = selectedSeriesName ?: "",
                        coverUrl = viewModel.coverRequest("/pages/${it.id}/0", "SMALL"),
                        onClick = onNavigateToReaderScreen,
                        downloadProgress = downloadProgressState[it.id],
                        cached = (cached[it.id] == true),
                        onDownloadClick = {
                            val issueId = it.id
                            val seriesId = selectedSeriesId ?: 0L
                            viewModel.download(seriesId, issueId)
                        }
                    )
                }
            }
        }
    }
}