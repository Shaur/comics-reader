package com.home.reader.api.dto

data class Series(
    val id: Long,
    val name: String,
    val cover: Long,
    val issuesCount: Long = 0,
    val completedIssues: Long = 0
)