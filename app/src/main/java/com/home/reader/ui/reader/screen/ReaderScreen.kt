package com.home.reader.ui.reader.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.KeepScreenOn
import com.home.reader.ui.reader.state.ReaderState
import com.home.reader.ui.reader.viewmodel.ReaderViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    id: Long,
    currentPage: Int = 0,
    lastPage: Int,
    mode: ReaderState.ReaderMode
) {

    val state by remember {
        viewModel.initState(id, currentPage, lastPage, mode)
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

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 3,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapPositionalThreshold = 0.15f
        )
    ) { page ->
        key(state.filler) {
            Image(
                painter = rememberAsyncImagePainter(
                    viewModel.requestPage(page),
                    filterQuality = FilterQuality.High
                ),
                contentDescription = "Page $page",
                contentScale = state.filler.scale,
                modifier = Modifier.filler(rememberScrollState(), rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { viewModel.resolverFiller() },
                            onTap = { offset ->
                                viewModel.resolveClick(screenWidthInPx, offset.x) {
                                    coroutineScope.launch {
                                        withContext(this.coroutineContext) {
                                            pagerState.animateScrollToPage(it)
                                        }
                                    }
                                }
                            }
                        )
                    }
            )
        }
    }

}

private fun Modifier.filler(
    horizontalScrollState: ScrollState,
    verticalScrollState: ScrollState
): Modifier {
    return this
        .fillMaxSize()
        .background(Color.Black)
        .verticalScroll(verticalScrollState)
        .horizontalScroll(horizontalScrollState)
}


