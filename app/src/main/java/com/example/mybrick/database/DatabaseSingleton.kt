package com.example.mybrick.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mybrick.database.dao.*
import com.example.mybrick.database.entity.*

@Database(entities = arrayOf(
    ItemType::class,
    Inventory::class,
    Color::class,
    Category::class,
    Code::class,
    InventoryPart::class,
    Part::class
), version = 4)
abstract class DatabaseSingleton : RoomDatabase() {
    abstract fun ItemTypesDAO (): ItemTypesDAO

    abstract fun InventoriesDAO (): InventoriesDAO

    abstract fun CodesDAO (): CodesDAO

    abstract fun InventoriesPartsDAO (): InventoriesPartsDAO

    abstract fun PartsDAO (): PartsDAO

    abstract fun CategoriesDAO (): CategoriesDAO

    abstract fun ColorsDAO(): ColorsDAO

    companion object{
        var INSTANCE: DatabaseSingleton? = null
        fun getInstance(context: Context): DatabaseSingleton {
            if (INSTANCE == null) {
                synchronized(DatabaseSingleton::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            DatabaseSingleton::class.java, "BrickListDatabase")
                            .createFromAsset("BrickList.db")
                            .build()
                    }
                }
            }
            return INSTANCE as DatabaseSingleton
        }
    }
}