package com.example.memori.database.dao

import androidx.room.*
import com.example.memori.database.entity.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE cardId = :id")
    suspend fun getCardById(id: Long): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<Card>
}