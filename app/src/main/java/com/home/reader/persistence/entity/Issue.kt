package com.home.reader.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
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
) : Comparable<Issue> {
    fun isRead(): Boolean {
        return currentPage == pagesCount - 1
    }

    override fun compareTo(other: Issue): Int {
        return this.issue.compareTo(other.issue)
    }
}
