package com.home.reader.utils

import androidx.compose.ui.unit.dp

object Constants {

    val COMICS_MIME_TYPES = arrayOf(
        "application/x-cbr",
        "application/x-cbz"
    )

    object RequestCodes {
        const val READ_EXTERNAL_STORAGE_CODE = 100
    }

    object SeriesExtra {
        const val SERIES_ID = "SERIES_ID"
        const val ISSUE_DIR = "ISSUE_DIR"
    }

    object Dirs {
        const val COVERS = "covers"
    }

    object Sizes {
        val COVER_WIDTH = 110.dp
        val COVER_HEIGHT = 177.dp
    }

    object ArgumentsPlaceholder {
        const val LAST_PAGE = "/{lastPage}"
        const val CURRENT_PAGE = "/{currentPage}"
        const val ISSUE_ID = "/{issueId}"
        const val SERIES_ID = "/{seriesId}"
        const val SERIES_NAME = "/{seriesName}"
    }

    object Argument {
        const val LAST_PAGE = "lastPage"
        const val CURRENT_PAGE = "currentPage"
        const val ISSUE_ID = "issueId"
        const val SERIES_ID = "seriesId"
        const val SERIES_NAME = "seriesName"
    }
}