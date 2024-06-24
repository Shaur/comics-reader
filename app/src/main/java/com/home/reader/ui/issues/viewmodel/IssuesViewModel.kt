package com.home.reader.ui.issues.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.component.dto.IssueDto
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.utils.coversPath
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.absolutePathString

class IssuesViewModel(
    context: Context,
    private val repository: IssueRepository
) : ViewModel() {

    var state = mutableStateOf(listOf<IssueDto>())

    private val coversDir = context.coversPath()
    private val dataDir = context.filesDir

    fun removeIssue(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
            dataDir.resolve(id.toString()).deleteRecursively()
        }
    }

    fun refresh(seriesId: Long) {
        viewModelScope.launch {
            val (series, issues) = repository.findAllBySeriesId(seriesId)
            val name = series.name

            val comparator = compareBy<Issue> { it.issue.length }.then(naturalOrder())
            state.value = issues.sortedWith(comparator).map {
                IssueDto(
                    id = it.id!!,
                    issue = it.issue,
                    seriesName = name,
                    pagesCount = it.pagesCount,
                    currentPage = it.currentPage,
                    coverPath = coversDir.resolve("${it.id!!}.jpg").absolutePathString()
                )
            }
        }
    }

    fun renameIssue(id: Long, issue: String) {
        viewModelScope.launch {
            val issueDto = state.value.first { it.id == id }
            if (issueDto.issue == issue) return@launch

            repository.updateIssue(id, issue)

            val filtered = state.value.filter { it.id != id }
            state.value = buildList {
                addAll(filtered)
                add(issueDto.copy(issue = issue))
            }
        }
    }

    fun markAsRead(id: Long?, postpone: suspend () -> Unit) {
        val issueDto = state.value.firstOrNull { it.id == id } ?: return

        viewModelScope.launch {
            val issue = repository.findById(issueDto.id) ?: return@launch
            issue.currentPage = issue.pagesCount - 1
            repository.update(issue)

            state.value = buildList {
                addAll(state.value - issueDto)
                add(issueDto.copy(currentPage = issue.currentPage))
            }.sortedBy { it.issue }

            postpone.invoke()
        }
    }

}