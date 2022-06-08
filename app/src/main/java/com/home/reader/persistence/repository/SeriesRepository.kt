package com.home.reader.persistence.repository

import com.home.reader.persistence.dao.IssueDao
import com.home.reader.persistence.dao.SeriesDao

class SeriesRepository(private val seriesDao: SeriesDao, private val issueDao: IssueDao) {

}