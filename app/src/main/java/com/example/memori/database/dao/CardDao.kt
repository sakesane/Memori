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
        WHERE deckId IN (:deckIds) 
        AND due < :until
        ORDER BY due ASC
    """)
    suspend fun getDueCardsByDecks(
        deckIds: List<Long>, 
        until: LocalDateTime
    ): List<Card>

    @Query("""
        SELECT * FROM cards
        WHERE deckId IN (:deckIds)
        AND status = 'New'
        ORDER BY cardId ASC
        LIMIT :limit
    """)
    suspend fun getNewCardsByDecks(
        deckIds: List<Long>, 
        limit: Int
    ): List<Card>

    @Query("SELECT deckId, COUNT(*) as newCount FROM cards WHERE status = 'New' GROUP BY deckId")
    suspend fun getNewCountByDeck(): List<DeckNewCount>

    // 虽然名字叫review，实际上是除了New之外的所有状态
    @Query("""
        SELECT deckId, COUNT(*) as reviewCount 
        FROM cards 
        WHERE status IN ('Learning', 'Review', 'Relearning') 
        GROUP BY deckId
    """)
    suspend fun getReviewCountByDeck(): List<DeckReviewCount>

    data class DeckNewCount(
        val deckId: Long,
        val newCount: Int? = 0
    )

    data class DeckReviewCount(
        val deckId: Long,
        val reviewCount: Int? = 0
    )
}