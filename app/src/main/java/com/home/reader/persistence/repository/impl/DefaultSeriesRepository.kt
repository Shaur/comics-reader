package com.home.reader.persistence.repository.impl

import com.home.reader.persistence.dao.SeriesDao
import com.home.reader.persistence.repository.SeriesRepository

class DefaultSeriesRepository(private val dao: SeriesDao) : SeriesRepository by dao