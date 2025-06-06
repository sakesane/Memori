package com.example.memori.database.dao

import androidx.room.*
import com.example.memori.database.entity.Revlog

@Dao
interface RevLogDao {
    @Insert
    suspend fun insertRevLog(revLog: Revlog): Long

    @Query("SELECT * FROM revlog WHERE cardId = :cardId ORDER BY review DESC")
    suspend fun getLogsByCardId(cardId: Long): List<Revlog>

    @Query("SELECT * FROM revlog")
    suspend fun getAllLogs(): List<Revlog>
}