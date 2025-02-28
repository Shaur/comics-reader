package com.home.reader.ui.catalogue.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.home.reader.api.dto.SeriesDto
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.catalogue.component.CatalogueIssueItem
import com.home.reader.ui.catalogue.component.CatalogueSeriesItem
import com.home.reader.ui.catalogue.viewmodel.CatalogueViewModel
import com.home.reader.ui.reader.state.ReaderState
import com.home.reader.utils.Constants.Sizes.COVER_WIDTH

@Composable
fun CatalogueScreen(
    viewModel: CatalogueViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToReaderScreen: (id: Long, currentPage: Int, lastPage: Int, mode: ReaderState.ReaderMode) -> Unit
) {

    val seriesState by remember { viewModel.seriesState }
    val issuesState by remember { viewModel.issuesState }
    var selectedSeries by remember { mutableStateOf<SeriesDto?>(null) }

    Column {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(COVER_WIDTH)
            ) {

                if (selectedSeries == null) {
                    items(seriesState) {
                        CatalogueSeriesItem(
                            item = it,
                            coverUrl = viewModel.coverRequest(it.cover),
                            onClick = { seriesId, _ ->
                                viewModel.refreshIssuesState(seriesId)
                                selectedSeries = it
                            }
                        )
                    }
                }

                if (selectedSeries != null) {
                    items(issuesState) {
                        CatalogueIssueItem(
                            item = it,
                            seriesName = selectedSeries?.title ?: "",
                            coverUrl = viewModel.coverRequest("/pages/${it.id}/0"),
                            onClick = onNavigateToReaderScreen,
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
}