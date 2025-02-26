package com.home.reader.ui.catalogue.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.reader.api.ApiHandler
import com.home.reader.api.dto.Issue
import com.home.reader.api.dto.Series
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatalogueViewModel(private val api: ApiHandler) : ViewModel() {

    var seriesState = mutableStateOf<List<Series>>(listOf())
    var issuesState = mutableStateOf<List<Issue>>(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            seriesState.value = api.getSeries()
        }
    }

    fun refreshIssuesState(seriesId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            issuesState.value = api.getIssues(seriesId)
        }
    }

    fun coverRequest(url: String): String = api.buildImageUrl(url)

}