package com.home.reader.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class Credentials(
    @PrimaryKey
    @ColumnInfo(name = "login")
    val login: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "token")
    val token: String
)
