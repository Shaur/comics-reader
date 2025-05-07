package com.home.reader.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.home.reader.persistence.entity.User
import com.home.reader.persistence.repository.UserRepository

@Dao
interface UserDao : UserRepository {

    @Query("select * from user where id = 1")
    override suspend fun get(): User?

    @Insert
    override suspend fun insert(user: User)

    @Update
    override suspend fun update(user: User)

    @Query("delete from user")
    override suspend fun delete()
}