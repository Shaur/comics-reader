package com.home.reader.ui.reader.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.ui.reader.state.ReaderState
import com.home.reader.ui.reader.state.ReaderState.Orientation.HORIZONTAL
import com.home.reader.ui.reader.state.ReaderState.Orientation.VERTICAL
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.geometry.Size

class ReaderViewModel(
    context: Context,
    private val issueRepository: IssueRepository
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

    fun loadPage(page: Int): File {
        val issueId = state.value.issueId.toString()
        val issueDir = contentDir.resolve(issueId)
        if (files.isEmpty()) {
            val comparator = compareBy<File> { it.nameWithoutExtension.length }.then(naturalOrder())
            files = issueDir.listFiles()?.sortedWith(comparator) ?: listOf()
        }

        return files[page]
    }

    fun initState(id: Long, currentPage: Int, lastPage: Int) {
        state.value = ReaderState(id, currentPage, lastPage)
    }

    fun updateCurrentPage(page: Int) {
        state.value = state.value.copy(currentPage = page)
        viewModelScope.launch {
            issueRepository.updateState(state.value.issueId, page)
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

    fun resolveOrientation(size: Size) {
        val orientation = if (size.width > size.height) HORIZONTAL else VERTICAL
        state.value = state.value.copy(orientation = orientation)
    }

}