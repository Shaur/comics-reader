package com.home.reader.api.dto

import java.util.Date

data class Issue(

    val id: Long,

    val number: String,

    val summary: String = "",

    val seriesId: Long,

    val pagesCount: Int,

    val currentPage: Int = 0,

    val publicationDate: Date
)

