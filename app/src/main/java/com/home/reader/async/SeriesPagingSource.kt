package com.home.reader.async

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.home.reader.api.ApiHandler
import com.home.reader.api.dto.SeriesDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeriesPagingSource(private val api: ApiHandler) : PagingSource<Int, SeriesDto>() {

    override fun getRefreshKey(state: PagingState<Int, SeriesDto>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SeriesDto> {
        val page = params.key ?: 0
        val pageSize = params.loadSize + 1

        Log.i("Page loader", "Load page $page with page size: $pageSize")

        try {
            val items = withContext(Dispatchers.IO) {
                api.getAllSeries(limit = pageSize, offset = page)
            }

            return LoadResult.Page(
                data = items.dropLast(1),
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (items.size <= params.loadSize) null else page + 1
            )
        } catch (ex: Exception) {
            return LoadResult.Error(ex)
        }
    }

}