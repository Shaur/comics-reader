package com.home.reader.persistence.repository.impl

import com.home.reader.persistence.dao.CredentialsDao
import com.home.reader.persistence.entity.Credentials
import com.home.reader.persistence.repository.CredentialsRepository
import kotlinx.coroutines.flow.Flow

class DefaultCredentialsRepository(private val dao: CredentialsDao) : CredentialsRepository {

    override fun findByLoginAndPassword(login: String, password: String): String? =
        dao.findByLoginAndPassword(login, password)

    override fun findFirstToken(): Flow<String?> = dao.findFirstToken()

    override suspend fun insert(credentials: Credentials) = dao.insert(credentials)

    override suspend fun update(credentials: Credentials) = dao.update(credentials)
}