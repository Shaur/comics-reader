package com.home.reader.ui.series.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil.request.ImageRequest
import com.home.reader.api.ApiProcessor
import com.home.reader.api.dto.Series
import com.home.reader.ui.common.GlobalState

class SeriesViewModel(
    private val api: ApiProcessor,
    private val globalState: MutableState<GlobalState>
) : ViewModel() {

    var state = mutableStateOf(listOf<Series>())
        private set

    fun getCover(issueId: Long): ImageRequest {
        return api.buildImageRequest(issueId, globalState.value.token!!, size = "small")
    }

    fun isOffline(): Boolean = !globalState.value.serviceAvailable

    fun refresh() {
        if (isOffline()) return

        Thread {
            val result = api.getSeries(globalState.value.token!!)
            if (result.isSuccess()) state.value = result.value!!
        }.start()
    }

}