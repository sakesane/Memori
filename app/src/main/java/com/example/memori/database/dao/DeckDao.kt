package com.example.memori.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.memori.database.entity.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    suspend fun getAll(): List<Deck>

    @Query("SELECT * FROM decks WHERE parentId = :parentId")
    suspend fun getChildren(parentId: Long): List<Deck>

    @Insert
    suspend fun insert(deck: Deck): Long

    @Delete
    suspend fun delete(deck: Deck)

    @Update
    suspend fun updateDeck(deck: Deck)

    @Query("SELECT * FROM decks")
    fun getAllFlow(): Flow<List<Deck>>

    @Query("SELECT * FROM decks WHERE deckId = :deckId LIMIT 1")
    fun getDeckFlow(deckId: Long): Flow<Deck?>

    @Query("SELECT * FROM decks WHERE deckId IN (:ids)")
    suspend fun getDecksByIds(ids: List<Long>): List<Deck>

    @Query("SELECT * FROM decks WHERE deckId = :deckId LIMIT 1")
    suspend fun getDeckById(deckId: Long): Deck?
}