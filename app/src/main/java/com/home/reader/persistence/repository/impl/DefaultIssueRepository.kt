package com.home.reader.persistence.repository.impl

import com.home.reader.persistence.dao.IssueDao
import com.home.reader.persistence.repository.IssueRepository

class DefaultIssueRepository(private val dao: IssueDao) : IssueRepository by dao