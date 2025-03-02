package com.home.reader.ui.reader.state

import androidx.compose.ui.layout.ContentScale
import com.home.reader.ui.reader.configuration.ReaderConfig.ReaderMode

data class ReaderState(
    val id: Long? = null,
    val externalId: Long? = null,
    val currentPage: Int,
    val lastPage: Int,
    val isLoading: Boolean = false,
    val filler: Filler = Filler.MAX_HEIGHT,
    val orientation: Orientation = Orientation.VERTICAL,
    val mode: ReaderMode = ReaderMode.LOCAL
) {
    enum class Filler(val scale: ContentScale) {
        MAX_HEIGHT(ContentScale.FillHeight),
        MAX_WIDTH(ContentScale.FillWidth)
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }

    fun getFileSourceId(): Long {
        if (mode == ReaderMode.REMOTE) {
            return externalId!!
        }

        return id!!
    }
}
