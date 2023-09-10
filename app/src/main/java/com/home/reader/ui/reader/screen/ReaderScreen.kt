package com.home.reader.ui.reader.screen

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.home.reader.ui.AppViewModelProvider
import com.home.reader.ui.common.component.Loader
import com.home.reader.ui.reader.viewmodel.ReaderViewModel
import com.home.reader.ui.reader.viewmodel.ReaderViewModel.LoaderState
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterialApi::class)
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

    val swipeableState = rememberSwipeableState(1)
    val screenWidthInPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenWidthInDp = LocalConfiguration.current.screenWidthDp
    val regularAnchors = mapOf(-screenWidthInPx to 0, 0f to 1, screenWidthInPx to 2)
    val firstPageAnchors = mapOf(-screenWidthInPx to 0, 0f to 1)
    val lastPageAnchors = mapOf(0f to 1, screenWidthInPx to 2)

    fun peekAnchors(): Map<Float, Int> {
        if (state.currentPage == 0) return firstPageAnchors
        if (state.currentPage == lastPage) return lastPageAnchors
        return regularAnchors
    }

    Loader(enable = state.isLoading)

    AsyncImage(
        model = viewModel.loadPage(),
        onLoading = { viewModel.loaderState(LoaderState.LOADING) },
        onSuccess = { viewModel.loaderState(LoaderState.SUCCESS) },
        contentDescription = "Page ${state.currentPage}",
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                anchors = peekAnchors(),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .offset {
                Log.i("State", swipeableState.currentValue.toString())
                IntOffset(swipeableState.offset.value.roundToInt(), 0)
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    viewModel.resolveClick(
                        width = screenWidthInDp,
                        x = it.x
                    )
                }

            }
    )


}