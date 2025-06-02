package com.example.memori.database.dao

import androidx.room.*
import com.example.memori.database.entity.Deck

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<Deck>

    @Insert
    suspend fun insert(deck: Deck): Long

    @Delete
    suspend fun delete(deck: Deck)
}