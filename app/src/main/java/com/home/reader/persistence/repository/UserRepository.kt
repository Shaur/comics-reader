package com.home.reader.persistence.repository

import com.home.reader.persistence.entity.User

interface UserRepository {

    suspend fun get(): User?

    suspend fun insert(user: User)

    suspend fun update(user: User)

}