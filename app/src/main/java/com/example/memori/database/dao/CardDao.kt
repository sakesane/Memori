package com.example.memori.database.dao

import androidx.room.*
import com.example.memori.database.entity.Card
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

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

    // 按传入的时间筛选到期卡片（排除 new 卡片）
    @Query("""
        SELECT * FROM cards 
        WHERE deckId IN (:deckIds) 
        AND due < :until
        AND status != 'New'
        ORDER BY due ASC
    """)
    suspend fun getDueCardsByDecks(
        deckIds: List<Long>, 
        until: LocalDateTime
    ): List<Card>

    // 筛选一定数量的New卡片，为了生成队列
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

    @Query("""
        SELECT * FROM cards 
        WHERE cardId = :cardId 
        LIMIT 1
    """)
    fun observeCardById(
        cardId: Long
    ): Flow<Card?>

    @Query("""
        SELECT COUNT(*) FROM cards 
        WHERE deckId IN (:deckIds) 
        AND status != 'New'
        AND due < :until
    """)
    suspend fun getDueReviewCountByDecks(
        deckIds: List<Long>,
        until: LocalDateTime
    ): Int

}