package com.example.mybrick.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mybrick.database.entity.InventoryPart

@Dao
interface InventoriesPartsDAO {
    @Insert
    fun insertPart(part: InventoryPart)

    @Delete
    fun delete(parts: List<InventoryPart>)

    @Query("SELECT * FROM InventoriesParts")
    fun findAll(): List<InventoryPart>

    fun deleteAll() {
        delete(findAll())
    }
}