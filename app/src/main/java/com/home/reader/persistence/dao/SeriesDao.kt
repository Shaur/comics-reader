package com.home.reader.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.entity.SeriesWithIssues
import com.home.reader.persistence.repository.SeriesRepository

@Dao
interface SeriesDao : SeriesRepository {

    @Transaction
    @Query(
        """
            select *
            from series
                left join issues on series.id = issues.series_id
            order by (pages_count - current_page - 1 = 0), name
        """
    )
    override suspend fun getAll(): Map<Series, List<Issue>>

    @Query("select * from series where lower(name) = lower(:name)")
    override suspend fun getSeriesByName(name: String): Series?

    @Query("select * from series where normalize_name = :normalizeName")
    override suspend fun getSeriesByNormalizeName(normalizeName: String): Series?

    @Query("select id from series where lower(name) = lower(:name)")
    override suspend fun getSeriesIdByName(name: String): Long?

    @Insert
    override suspend fun insertAll(vararg series: Series)

    @Insert
    override suspend fun insert(series: Series): Long

    @Delete
    override fun delete(series: Series)

    @Transaction
    @Query("select * from series where id = :seriesId")
    override suspend fun getSeriesById(seriesId: Long): SeriesWithIssues

    @Query("update series set name = :newName where id = :id")
    override suspend fun updateSeriesName(id: Long, newName: String)

    @Query("delete from series where id = :id")
    override suspend fun deleteById(id: Long)
}