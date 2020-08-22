package com.example.mybrick.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.mybrick.database.entity.Color

@Dao
interface ColorsDao {
    @Query("SELECT * FROM Colors")
    fun getAll(): List<Color>

    @Query("SELECT * FROM Colors WHERE Code = :colorCode")
    fun findByCode(colorCode: Int): Color

}