package com.home.reader.utils

import androidx.compose.ui.unit.dp

object Constants {

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
        const val PREVIEW_COVER_WIDTH_IN_DP = 110
        val COVER_WIDTH = 110.dp
        const val PREVIEW_GATTER_WIDTH_ID_DP = 10
    }

    object ArgumentsPlaceholder {
        const val LAST_PAGE = "/{lastPage}"
        const val CURRENT_PAGE = "/{currentPage}"
        const val ISSUE_ID = "/{issueId}"
        const val SERIES_ID = "/{seriesId}"
    }

    object Argument {
        const val LAST_PAGE = "lastPage"
        const val CURRENT_PAGE = "currentPage"
        const val ISSUE_ID = "issueId"
        const val SERIES_ID = "seriesId"
    }
}