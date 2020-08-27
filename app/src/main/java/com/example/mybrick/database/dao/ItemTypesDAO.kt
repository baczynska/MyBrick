package com.example.mybrick.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.mybrick.database.entity.ItemType

@Dao
interface ItemTypesDAO {
    @Query("SELECT * FROM ItemTypes")
    fun getAll(): List<ItemType>

    @Query("SELECT id FROM ItemTypes WHERE Code = :code LIMIT 1")
    fun findIdByItemType(code: String): Int?
}