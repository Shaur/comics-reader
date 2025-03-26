package com.home.reader.utils

import androidx.compose.ui.unit.dp

object Constants {

    val COMICS_MIME_TYPES = arrayOf(
        "application/x-cbr",
        "application/x-cbz",
        "application/vnd.comicbook-rar",
        "application/vnd.comicbook+zip"
    )

    object Dirs {
        const val COVERS = "covers"
    }

    object Sizes {
        val COVER_WIDTH = 110.dp
        val COVER_HEIGHT = 177.dp
    }

    const val DEFAULT_PAGE_SIZE = 20

}