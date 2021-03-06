package com.example.mybrick.database.dao

import androidx.room.*
import com.example.mybrick.database.entity.Inventory
import java.time.Instant

@Dao
interface InventoriesDAO {
    @Query("SELECT * FROM Inventories WHERE Name = :name")
    fun getByName(name: String): Inventory

    @Query("SELECT * FROM Inventories WHERE :alsoArchived OR Active")
    fun findAll(alsoArchived: Boolean = true): List<Inventory>

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Id = :id)")
    fun checkById(id: Int): Boolean

    @Query("SELECT EXISTS(SELECT * FROM Inventories WHERE Name = :name)")
    fun checkByName(name: String): Boolean

    @Insert
    fun insert(newInventory: Inventory)

    @Delete
    fun delete(inventories: Inventory)

    @Delete
    fun deleteList(inventories: List<Inventory>)

    @Query("SELECT Id FROM Inventories WHERE Name = :name LIMIT 1")
    fun idByName(name: String): Int?


    @Update
    fun update(inventory: Inventory)

    fun deleteThis(name: String){
        delete(getByName(name))
    }

    fun deleteAll() {
        deleteList(findAll())
    }

    fun updateLastAccessTime(inventoryName: String) {
        update(getByName(inventoryName).let {
            Inventory(it.id, it.name, it.active, Instant.now().epochSecond.toInt())
        })
    }

    fun changeArchiveStatus(inventoryName: String) {
        update(getByName(inventoryName).let {
            Inventory(it.id, it.name, if (it.active == 1) 0 else 1, it.lastAccess)
        })
    }
}