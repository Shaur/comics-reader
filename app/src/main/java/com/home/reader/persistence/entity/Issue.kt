package com.home.reader.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issues")
data class Issue(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    @ColumnInfo(name = "issue")
    var issue: String,

    @ColumnInfo(name = "series_id")
    var seriesId: Long,

    @ColumnInfo(name = "pages_count")
    var pagesCount: Int = 0,

    @ColumnInfo(name = "current_page")
    var currentPage: Int = 0
) {
    fun isRead(): Boolean {
        return currentPage == pagesCount - 1
    }
}