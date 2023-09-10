package com.home.reader.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.home.reader.persistence.entity.Credentials
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialsDao {

    @Query("select token from credentials where login = :login and password = :password")
    fun findByLoginAndPassword(login: String, password: String): String?

    @Query("select token from credentials limit 1")
    fun findFirstToken(): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(credentials: Credentials)

    @Update
    suspend fun update(credentials: Credentials)
}