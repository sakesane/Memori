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

    // 按传入的时间筛选到期卡片
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

    // 获取deck当天复习卡的count，筛选到期时间
    @Query("""
        SELECT deckId, COUNT(*) as reviewCount 
        FROM cards 
        WHERE status IN ('Learning', 'Review', 'Relearning') 
        AND due < :until
        GROUP BY deckId
    """)
    suspend fun getReviewCountByDeck(
        until: LocalDateTime
    ): List<DeckReviewCount>
    // 注：获取deck所有New的count是通过查询new卡片队列大小实现的

    // 获取deck所有New的count，不筛选当天到期
    @Query("SELECT deckId, COUNT(*) as newCount FROM cards WHERE status = 'New' GROUP BY deckId")
    suspend fun getAllNewCountByDeck(): List<DeckNewCount>

    // 获取deck所有复习卡的count，不筛选当天到期
    // 虽然名字叫review，实际上是除了New之外的所有状态
    @Query("""
        SELECT deckId, COUNT(*) as reviewCount 
        FROM cards 
        WHERE status IN ('Learning', 'Review', 'Relearning') 
        GROUP BY deckId
    """)
    suspend fun getAllReviewCountByDeck(): List<DeckReviewCount>

    @Query("SELECT * FROM card WHERE cardId = :cardId LIMIT 1")
    fun observeCardById(cardId: Long): Flow<Card?>

    data class DeckNewCount(
        val deckId: Long,
        val newCount: Int? = 0
    )

    data class DeckReviewCount(
        val deckId: Long,
        val reviewCount: Int? = 0
    )
}