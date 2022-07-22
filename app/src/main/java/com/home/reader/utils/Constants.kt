package com.home.reader.utils

object Constants {
    const val CBR_CONTENT_TYPE = "application/x-cbr"
    const val CBZ_CONTENT_TYPE = "application/x-cbz"

    val COMICS_MIME_TYPES = arrayOf(CBR_CONTENT_TYPE, CBZ_CONTENT_TYPE)

    object RequestCodes {
        const val READ_EXTERNAL_STORAGE_CODE = 100
        const val BACK_CODE = 50
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
        const val PREVIEW_GATTER_WIDTH_ID_DP = 10
    }

    object XmlPath {
        const val SERIES = "/ComicInfo/Series/text()"
        const val TITLE = "/ComicInfo/Title/text()"
    }
}