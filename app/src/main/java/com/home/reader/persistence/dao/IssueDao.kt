package com.home.reader.persistence.dao

import androidx.room.*
import com.home.reader.persistence.entity.Issue

@Dao
interface IssueDao {

    @Query("select * from issues where id = :id")
    suspend fun findById(id: Long): Issue?

    @Query("select * from issues where series_id = :seriesId and issue = :issue")
    suspend fun findBySeriesIdAndIssue(seriesId: Long, issue: String): Issue?

    @Insert
    suspend fun insert(issue: Issue): Long

    @Update
    suspend fun update(issue: Issue)

    @Query("update issues set pages_count = :pagesCount where id = :issueId")
    suspend fun updatePagesCount(issueId: Long, pagesCount: Int)

    @Delete
    suspend fun delete(issue: Issue)
}