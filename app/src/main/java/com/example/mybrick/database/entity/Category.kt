package com.example.mybrick.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Category(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "Code") val code: Int,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "NamePL") val namePL: String?
)