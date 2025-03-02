package com.home.reader.ui.reader.configuration

import kotlinx.serialization.Serializable

@Serializable
data class ReaderConfig(
    val id: Long? = null,
    val externalId: Long? = null,
    val currentPage: Int = 0,
    val lastPage: Int
) {
    enum class ReaderMode {
        LOCAL, REMOTE, CACHED
    }
}
