package com.home.reader.async

data class UpdateReadingStateTask(
    val issueId: Long,
    val page: Int
)
