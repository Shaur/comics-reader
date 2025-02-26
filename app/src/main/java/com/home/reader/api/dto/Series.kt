package com.home.reader.api.dto

data class Series(
    val id: Long,
    val title: String,
    val publisher: String,
    val issuesCount: Int,
    val cover: String,
    val subscribed: Boolean = false
)