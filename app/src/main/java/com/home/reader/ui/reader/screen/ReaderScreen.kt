package com.home.reader.ui.reader.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.KeepScreenOn
import com.home.reader.ui.reader.viewmodel.ReaderViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    id: Long,
    currentPage: Int = 0,
    lastPage: Int
) {

    val state by remember {
        viewModel.initState(id, currentPage, lastPage)
        viewModel.state
    }

    val pagerState = rememberPagerState(
        pageCount = { state.lastPage + 1 },
        initialPage = state.currentPage
    )

    val screenWidthInPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val coroutineScope = rememberCoroutineScope()

    KeepScreenOn()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.updateCurrentPage(page)
        }
    }

    HorizontalPager(state = pagerState) { page ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        viewModel.resolveClick(
                            width = screenWidthInPx,
                            x = offset.x
                        ) {
                            coroutineScope.launch {
                                withContext(this.coroutineContext) {
                                    pagerState.animateScrollToPage(it)
                                }
                            }
                        }
                    }
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(viewModel.loadPage(page)),
                contentDescription = "Page $page",
                modifier = Modifier.fillMaxHeight()
            )
        }
    }

}