package com.home.reader.persistence.repository

import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.entity.SeriesWithIssues

interface SeriesRepository {

    suspend fun getAll(): Map<Series, List<Issue>>

    suspend fun getSeriesByName(name: String): Series?

    suspend fun getSeriesByNormalizeName(normalizeName: String): Series?

    suspend fun getSeriesIdByName(name: String): Long?

    suspend fun insertAll(vararg series: Series)

    suspend fun insert(series: Series): Long

    fun delete(series: Series)

    suspend fun getSeriesById(seriesId: Long): SeriesWithIssues

    suspend fun updateSeriesName(id: Long, newName: String)

    suspend fun deleteById(id: Long)

    suspend fun getByExternalId(id: Long): Series?
}