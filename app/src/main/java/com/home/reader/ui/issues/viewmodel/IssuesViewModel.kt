package com.home.reader.ui.issues.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil.request.ImageRequest
import com.home.reader.api.ApiProcessor
import com.home.reader.api.dto.Issue
import com.home.reader.ui.common.GlobalState

class IssuesViewModel(
    private val api: ApiProcessor,
    private val globalState: MutableState<GlobalState>
) : ViewModel() {

    var state = mutableStateOf(listOf<Issue>())
        private set

    fun getCover(issueId: Long): ImageRequest {
        return api.buildImageRequest(issueId, globalState.value.token!!, size = "small")
    }

    fun refresh(seriesId: Long) {
        Thread {
            val result = api.getIssues(seriesId, globalState.value.token!!)
            if (result.isSuccess()) state.value = result.value!!
        }.start()
    }

}