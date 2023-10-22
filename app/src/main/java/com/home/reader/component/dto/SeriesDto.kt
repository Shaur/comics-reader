package com.home.reader.component.dto

data class SeriesDto(
    val id: Long,
    val name: String,
    val issuesCount: Int = 0,
    val completedIssues: Int = 0,
    val coverPath: String = ""
)