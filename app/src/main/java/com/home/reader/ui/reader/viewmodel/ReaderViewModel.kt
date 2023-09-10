package com.home.reader.ui.reader.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil.request.ImageRequest
import com.home.reader.api.ApiProcessor
import com.home.reader.ui.common.GlobalState
import com.home.reader.ui.reader.state.ReaderState

class ReaderViewModel(
    private val api: ApiProcessor,
    private val globalState: MutableState<GlobalState>
) : ViewModel() {

    val state = mutableStateOf(
        ReaderState(
            issueId = 0,
            currentPage = 0,
            lastPage = 0
        )
    )

    fun loadPage(): ImageRequest {
        return api.buildImageRequest(
            issueId = state.value.issueId,
            page = state.value.currentPage,
            token = globalState.value.token!!
        )
    }

    fun initState(id: Long, currentPage: Int, lastPage: Int) {
        state.value = ReaderState(id, currentPage, lastPage)
    }

    fun nextPage() {
        val currentPage = state.value.currentPage
        if (currentPage == state.value.lastPage) return

        state.value = state.value.copy(currentPage = currentPage + 1)
    }

    fun prevPage() {
        val currentPage = state.value.currentPage
        if (currentPage == 0) return

        state.value = state.value.copy(currentPage = currentPage - 1)
    }

    fun resolveClick(width: Int, x: Float) {
        if (x < width / 2f && state.value.currentPage > 0) {
            prevPage()
        } else if (x > width / 2f && state.value.currentPage != state.value.lastPage) {
            nextPage()
        }
    }

    fun loaderState(loaderState: LoaderState) {
        state.value = state.value.copy(isLoading = loaderState != LoaderState.SUCCESS)
    }

    enum class LoaderState {
        SUCCESS,
        LOADING
    }

}