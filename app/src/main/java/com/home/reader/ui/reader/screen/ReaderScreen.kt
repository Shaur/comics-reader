package com.home.reader.ui.reader.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.KeepScreenOn
import com.home.reader.ui.reader.state.ReaderState
import com.home.reader.ui.reader.state.ReaderState.Filler.MAX_HEIGHT
import com.home.reader.ui.reader.state.ReaderState.Filler.MAX_WIDTH
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


    HorizontalPager(state = pagerState/*, modifier = Modifier.fillMaxSize()*/) { page ->
        Image(
            painter = rememberAsyncImagePainter(
                viewModel.loadPage(page),
                contentScale = if (state.filler == MAX_HEIGHT) ContentScale.FillHeight else ContentScale.FillWidth
            ),
            contentDescription = "Page $page",
            contentScale = if (state.filler == MAX_HEIGHT) ContentScale.FillHeight else ContentScale.FillWidth,
            modifier = filler(state.filler, rememberScrollState())
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

private fun filler(
    filler: ReaderState.Filler,
    scrollState: ScrollState
): Modifier {
    val modifier = Modifier.fillMaxSize().background(Color.Black)
    return if (filler == MAX_WIDTH) {
        modifier.verticalScroll(scrollState)
    } else {
        modifier
    }
}