package com.home.reader.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.home.reader.ReaderApplication
import com.home.reader.ui.issues.viewmodel.IssuesViewModel
import com.home.reader.ui.login.viewmodel.LoginViewModel
import com.home.reader.ui.reader.viewmodel.ReaderViewModel
import com.home.reader.ui.series.viewmodel.SeriesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                repository = readerApplication().container.credentialsRepository,
                api = readerApplication().container.api,
                globalState = readerApplication().container.globalSate
            )
        }

        initializer {
            SeriesViewModel(
                api = readerApplication().container.api,
                globalState = readerApplication().container.globalSate
            )
        }

        initializer {
            IssuesViewModel(
                api = readerApplication().container.api,
                globalState = readerApplication().container.globalSate
            )
        }

        initializer {
            ReaderViewModel(
                api = readerApplication().container.api,
                globalState = readerApplication().container.globalSate
            )
        }
    }
}

fun CreationExtras.readerApplication(): ReaderApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ReaderApplication)