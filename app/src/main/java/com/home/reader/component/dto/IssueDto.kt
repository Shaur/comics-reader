package com.home.reader.component.dto

data class IssueDto(
    val id: Long,
    val issue: String,
    val seriesName: String,
    val externalId: Long? = null,
    val pagesCount: Int = 0,
    val currentPage: Int = 0,
    val coverPath: String = ""
)
