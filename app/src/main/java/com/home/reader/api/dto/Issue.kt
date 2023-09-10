package com.home.reader.api.dto

data class Issue(
    val id: Long,
    val number: String,
    val pagesCount: Int,
    val currentPage: Int = 0
)