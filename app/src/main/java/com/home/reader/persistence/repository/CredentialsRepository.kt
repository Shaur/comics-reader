package com.home.reader.persistence.repository

import com.home.reader.persistence.entity.Credentials
import kotlinx.coroutines.flow.Flow

interface CredentialsRepository {

    fun findByLoginAndPassword(login: String, password: String): String?

    fun findFirstToken(): Flow<String?>

    suspend fun insert(credentials: Credentials)

    suspend fun update(credentials: Credentials)

}