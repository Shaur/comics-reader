package com.home.reader.ui.reader.state

data class ReaderState(
    val issueId: Long,
    val currentPage: Int,
    val lastPage: Int,
    val isLoading: Boolean = false,
    val filler: Filler = Filler.MAX_HEIGHT
) {
    enum class Filler {
        MAX_HEIGHT, MAX_WIDTH
    }
}
