package com.home.reader.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.home.reader.ReaderApplication
import com.home.reader.ui.issues.viewmodel.IssuesViewModel
import com.home.reader.ui.reader.viewmodel.ReaderViewModel
import com.home.reader.ui.series.viewmodel.SeriesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            SeriesViewModel(
                context = readerApplication().applicationContext,
                repository = readerApplication().container.seriesRepository,
                issueRepository = readerApplication().container.issueRepository
            )
        }

        initializer {
            IssuesViewModel(
                context = readerApplication().applicationContext,
                repository = readerApplication().container.issueRepository
            )
        }

        initializer {
            ReaderViewModel(
                context = readerApplication().applicationContext,
                issueRepository = readerApplication().container.issueRepository
            )
        }
    }
}

fun CreationExtras.readerApplication(): ReaderApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ReaderApplication)