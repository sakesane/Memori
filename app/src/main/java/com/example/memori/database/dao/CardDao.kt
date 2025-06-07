package com.example.memori.database.dao

import androidx.room.*
import com.example.memori.database.entity.Card
import java.time.LocalDateTime

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

    @Query("""
        SELECT * FROM cards 
        WHERE deckId = :deckId 
        AND due < :until
        ORDER BY due ASC
    """)
    suspend fun getDueCardsByDeck(
        deckId: Long,
        until: LocalDateTime
    ): List<Card>

    @Query("""
        SELECT * FROM cards
        WHERE deckId = :deckId
        AND status = 'New'
        ORDER BY cardId ASC
        LIMIT :limit
    """)
    suspend fun getNewCardsByDeck(
        deckId: Long,
        limit: Int
    ): List<Card>
}