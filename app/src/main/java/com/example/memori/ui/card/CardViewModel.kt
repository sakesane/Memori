package com.example.memori.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.memori.algorithm.FSRS
import com.example.memori.algorithm.Rating
import com.example.memori.database.entity.toFSRSCard
import com.example.memori.database.entity.toCard
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
import androidx.lifecycle.liveData

@HiltViewModel
class CardViewModel @Inject constructor(
    private val db: MemoriDB
) : ViewModel() {

    private val cardDao = db.cardDao()
    private val deckDao = db.deckDao()

    private val _cardQueue = MutableLiveData<List<Card>>()
    val cardQueue: LiveData<List<Card>> = _cardQueue

    /**
     * 递归获取指定deck及其所有子deck的id
     */
    private suspend fun getAllSubDeckIds(rootId: Long): List<Long> {
        val result = mutableListOf<Long>()
        suspend fun collect(id: Long) {
            result.add(id)
            val children = deckDao.getChildren(id)
            children.forEach { collect(it.deckId) }
        }
        collect(rootId)
        return result
    }

    /**
     * 获取指定deck及其所有子deck下的复习卡片和新卡片队列
     * @param deckId 目标卡组ID
     * @param until 截止时间
     */
    fun loadCardQueue(deckId: Long, until: LocalDateTime) {
        viewModelScope.launch {
            // 1. 获取所有相关deckId（包括子deck）
            val allDeckIds = getAllSubDeckIds(deckId)
            // 2. 获取deck，读取新卡上限
            val deck = deckDao.getAll().find { it.deckId == deckId }
            val newCount = deck?.newCount ?: 0

            // 3. 获取复习卡片
            val dueCards = cardDao.getDueCardsByDecks(allDeckIds, until)
            // 4. 获取新卡片
            val newCards = cardDao.getNewCardsByDecks(allDeckIds, newCount)

            // 5. 合并队列
            val queue = mergeCards(dueCards, newCards)
            _cardQueue.postValue(queue)
        }
    }

    // 队列合并逻辑
    private fun mergeCards(due: List<Card>, new: List<Card>): List<Card> {
        return due + new
    }

    /**
     * 用户评分反馈处理
     * @param card 当前卡片
     * @param rating 用户评分
     */
    fun onCardRated(card: Card, rating: Rating, until: LocalDateTime) {
        viewModelScope.launch(Dispatchers.IO) {
            println("原始Card: $card")
            val fsrsCard = card.toFSRSCard()
            println("转换为FSRSCard: $fsrsCard")
            val now = LocalDateTime.now()
            val fsrs = FSRS()
            val schedulingMap = fsrs.repeat(fsrsCard, now)
            println("schedulingMap keys: ${schedulingMap.keys}")
            println("用户评分: $rating")
            val schedulingInfo = schedulingMap[rating]
            if (schedulingInfo == null) {
                println("未找到对应的SchedulingInfo，rating=$rating")
                return@launch
            }
            val updatedFSRSCard = schedulingInfo.card
            println("更新后的FSRSCard: $updatedFSRSCard")
            val updatedCard = updatedFSRSCard.toCard(
                cardId = card.cardId,
                deckId = card.deckId
            ).copy(
                front = card.front,
                back = card.back,
                example = card.example
            )
            println("最终用于更新的Card: $updatedCard")
            cardDao.updateCard(updatedCard)
            println("数据库已更新")

            // 在新卡被学习时递归向上更新newCount
            if (card.status == "New" && updatedCard.status != "New") {
                cascadeDecreaseNewCount(card.deckId)
            }

            loadCardQueue(card.deckId, until)
        }
    }

    private suspend fun cascadeDecreaseNewCount(deckId: Long) {
        var currentDeckId: Long? = deckId
        val allDecks = deckDao.getAll()
        while (currentDeckId != null) {
            val deck = allDecks.find { it.deckId == currentDeckId }
            if (deck != null && deck.newCount!! > 0) {
                deck.newCount = deck.newCount!! - 1
                deckDao.updateDeck(deck)
                currentDeckId = deck.parentId
            } else {
                break
            }
        }
    }

    // 获取某张卡片的 due 字段，自动响应数据库变化
    fun getCardDueLive(cardId: Long): LiveData<LocalDateTime> = liveData {
        cardDao.observeCardById(cardId).collect { card ->
            emit(card?.due ?: LocalDateTime.now())
        }
    }
}