package com.home.reader.persistence.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class SeriesWithIssues(
    @Embedded
    val series: Series,

    @Relation(
        parentColumn = "id",
        entityColumn = "series_id"
    )
    val issues: List<Issue>
)
