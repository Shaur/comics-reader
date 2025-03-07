package com.home.reader.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class Series(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "normalize_name")
    var normalizeName: String? = null,
    @ColumnInfo(name = "external_id", index = true)
    var externalId: Long? = null
)
