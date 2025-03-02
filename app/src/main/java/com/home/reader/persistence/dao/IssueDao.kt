package com.home.reader.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.persistence.repository.IssueRepository

@Dao
interface IssueDao : IssueRepository {

    @Transaction
    @Query("select * from series where id = :seriesId")
    override suspend fun findAllBySeriesId(seriesId: Long): SeriesWithIssues

    @Query("select * from issues where id = :id")
    override suspend fun findById(id: Long): Issue?

    @Query("select * from issues where series_id = :seriesId and issue = :issue")
    override suspend fun findBySeriesIdAndIssue(seriesId: Long, issue: String): Issue?

    @Query("select * from issues where series_id = :seriesId order by issue desc")
    override suspend fun findLastIssueBySeriesId(seriesId: Long): Issue?

    @Insert
    override suspend fun insert(issue: Issue): Long

    @Update
    override suspend fun update(issue: Issue)

    @Query("update issues set pages_count = :pagesCount where id = :issueId")
    override suspend fun updatePagesCount(issueId: Long, pagesCount: Int)

    @Query("update issues set current_page = :page where id = :issueId")
    override suspend fun updateState(issueId: Long, page: Int)

    @Delete
    override suspend fun delete(issue: Issue)

    @Query("delete from issues where id = :id")
    override suspend fun delete(id: Long)

    @Query("update issues set issue = :issue where id = :id")
    override suspend fun updateIssue(id: Long, issue: String)

    @Query("update issues set series_id = :newId where series_id = :oldId")
    override suspend fun changeSeriesId(oldId: Long, newId: Long)

    @Query("select * from issues where external_id in (:externalIds)")
    override suspend fun getCached(externalIds: Set<Long>): List<Issue>
}