package com.home.reader.persistence.repository.impl

import com.home.reader.persistence.dao.UserDao
import com.home.reader.persistence.repository.UserRepository

class DefaultUserRepository(private val dao: UserDao) : UserRepository by dao