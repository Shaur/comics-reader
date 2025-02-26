package com.home.reader.ui.reader.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.ui.reader.state.ReaderState
import kotlinx.coroutines.launch
import java.io.File
import coil.request.ImageRequest
import com.home.reader.api.ApiHandler
import kotlinx.coroutines.Dispatchers

class ReaderViewModel(
    context: Context,
    private val issueRepository: IssueRepository,
    private val api: ApiHandler
) : ViewModel() {

    val state = mutableStateOf(
        ReaderState(
            issueId = 0,
            currentPage = 0,
            lastPage = 0
        )
    )

    private val contentDir = context.filesDir

    private var files: List<File> = listOf()

    private val imageRequestBuilder = ImageRequest.Builder(context)

    private fun loadLocalPage(page: Int): File {
        val issueId = state.value.issueId.toString()
        val issueDir = contentDir.resolve(issueId)
        if (files.isEmpty()) {
            val comparator = compareBy<File> { it.nameWithoutExtension.length }.then(naturalOrder())
            files = issueDir.listFiles()?.sortedWith(comparator) ?: listOf()
        }

        return files[page]
    }

    fun requestPage(page: Int): ImageRequest {
        if (state.value.mode == ReaderState.ReaderMode.REMOTE) {
            val issueId = state.value.issueId.toString()
            return imageRequestBuilder.data(api.buildImageUrl("/pages/$issueId/$page")).build()
        }

        return imageRequestBuilder.data(loadLocalPage(page)).build()
    }

    fun initState(id: Long, currentPage: Int, lastPage: Int, mode: ReaderState.ReaderMode) {
        state.value = ReaderState(id, currentPage, lastPage, mode = mode)
    }

    fun updateCurrentPage(page: Int) {
        state.value = state.value.copy(currentPage = page)
        viewModelScope.launch(Dispatchers.IO) {
            if (state.value.mode == ReaderState.ReaderMode.REMOTE) {
                api.updateProgress(state.value.issueId, currentPage = page)
            } else {
                issueRepository.updateState(state.value.issueId, page)
            }
        }
    }

    private fun nextPage(action: (Int) -> Unit) {
        val currentPage = state.value.currentPage
        if (currentPage == state.value.lastPage) return

        state.value = state.value.copy(currentPage = currentPage + 1)
        action.invoke(state.value.currentPage)
    }

    private fun prevPage(action: (Int) -> Unit) {
        val currentPage = state.value.currentPage
        if (currentPage == 0) return

        state.value = state.value.copy(currentPage = currentPage - 1)
        action.invoke(state.value.currentPage)
    }

    fun resolveClick(width: Float, x: Float, action: (Int) -> Unit) {
        if (x < width / 2 && state.value.currentPage > 0) {
            prevPage(action)
        } else if (x > width / 2 && state.value.currentPage != state.value.lastPage) {
            nextPage(action)
        }
    }

    fun resolverFiller() {
        if (state.value.filler == ReaderState.Filler.MAX_HEIGHT) {
            state.value = state.value.copy(filler = ReaderState.Filler.MAX_WIDTH)
        } else {
            state.value = state.value.copy(filler = ReaderState.Filler.MAX_HEIGHT)
        }
    }

}