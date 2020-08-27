package com.example.mybrick.database.dao

import androidx.room.*
import com.example.mybrick.database.entity.Code

@Dao
interface CodesDAO {
    @Query("SELECT * FROM Codes WHERE ItemID = :itemId AND ColorID = :colorId LIMIT 1")
    fun findByItemIdAndColorId(itemId: Int, colorId: Int): Code?

    @Query("SELECT * FROM Codes WHERE ItemID = :itemId LIMIT 1")
    fun findByItemId(itemId: Int): Code?

    @Insert
    fun insertNewCode(parts: Code): Long

    @Update
    fun updateCode(parts: Code)
}