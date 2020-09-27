package com.example.mybrick.database.dao

import androidx.room.*
import com.example.mybrick.database.entity.Inventory
import com.example.mybrick.database.entity.InventoryPart

@Dao
interface InventoriesPartsDAO {
    @Insert
    fun insertPart(part: InventoryPart)

//    @Query("SELECT * FROM InventoriesParts WHERE Name = :name")
//    fun getByName(name: String): Inventory

    @Delete
    fun delete(inventories: Inventory)

    @Delete
    fun deleteList(parts: List<InventoryPart>)

    @Query("SELECT * FROM InventoriesParts")
    fun findAll(): List<InventoryPart>

    @Query("SELECT * FROM InventoriesParts WHERE InventoryID = :inventoryId")
    fun findAllByInventoryId(inventoryId: Int): List<InventoryPart>

    fun deleteAll() {
        deleteList(findAll())
    }

    @Update
    fun update(parts: List<InventoryPart>)

    fun deteteForThis(id: Int){
        deleteList(findAllByInventoryId(id))
    }
}