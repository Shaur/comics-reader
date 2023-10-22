package com.home.reader.persistence.repository

import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.SeriesWithIssues

interface IssueRepository {

    suspend fun findAllBySeriesId(seriesId: Long): SeriesWithIssues

    suspend fun findById(id: Long): Issue?

    suspend fun findBySeriesIdAndIssue(seriesId: Long, issue: String): Issue?

    suspend fun findLastIssueBySeriesId(seriesId: Long): Issue?

    suspend fun insert(issue: Issue): Long

    suspend fun update(issue: Issue)

    suspend fun updatePagesCount(issueId: Long, pagesCount: Int)

    suspend fun updateState(issueId: Long, page: Int)

    suspend fun delete(issue: Issue)

    suspend fun delete(id: Long)

    suspend fun updateIssue(id: Long, issue: String)

    suspend fun changeSeriesId(oldId: Long, newId: Long)
}