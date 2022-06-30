package com.home.reader.persistence.dao

import androidx.room.*
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.entity.SeriesWithIssues

@Dao
interface SeriesDao {

    @Transaction
    @Query(
        """
            select *
            from series
                left join issues on series.id = issues.series_id
            order by (pages_count - current_page - 1 = 0), name
        """
    )
    suspend fun getAll(): Map<Series, List<Issue>>

    @Query("select * from series where name = :name")
    suspend fun getSeriesByName(name: String): Series?

    @Query("select id from series where name = :name")
    suspend fun getSeriesIdByName(name: String): Long?

    @Insert
    suspend fun insertAll(vararg series: Series)

    @Insert
    suspend fun insert(series: Series): Long

    @Delete
    fun delete(series: Series)

    @Transaction
    @Query("select * from series where id = :seriesId")
    suspend fun getSeriesById(seriesId: Long): SeriesWithIssues

}