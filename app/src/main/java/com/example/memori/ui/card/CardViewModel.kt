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
            println("获取到的所有相关deckId: $allDeckIds")

            // 2. 获取deck，读取新卡上限
            val deck = deckDao.getAll().find { it.deckId == deckId }
            val newCount = deck?.newCount ?: 0
            println("当前deck: $deck, newCount: $newCount")

            // 3. 获取复习卡片
            val dueCards = cardDao.getDueCardsByDecks(allDeckIds, until)
            println("获取到的复习卡片数量: ${dueCards.size}")

            // 4. 获取新卡片
            val newCards = cardDao.getNewCardsByDecks(allDeckIds, newCount)
            println("获取到的新卡片数量: ${newCards.size}")

            // 5. 合并队列
            val queue = mergeCards(dueCards, newCards)
            println("最终合并队列数量: ${queue.size}")
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
                word = card.word,
                IPA = card.IPA,
                definition = card.definition,
                exampleEN = card.exampleEN,
                exampleCN = card.exampleCN,
                mnemonic = card.mnemonic,
                add = card.add
            )
            println("最终用于更新的Card: $updatedCard")
            cardDao.updateCard(updatedCard)
            println("数据库已更新")

            // 在新卡被学习时递归向上更新 newCount
            if (card.status == "New" && updatedCard.status != "New") {
                cascadeDecreaseNewCount(card.deckId)
            }

            // 每学习一张卡片都递归更新reviewCount
            cascadeComputeReviewCount(card.deckId, until)

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

    private suspend fun cascadeComputeReviewCount(deckId: Long, until: LocalDateTime) {
        var currentDeckId: Long? = deckId
        val allDecks = deckDao.getAll()
        println("开始递归更新reviewCount，起始deckId: $deckId")
        while (currentDeckId != null) {
            println("处理deckId: $currentDeckId")
            val subDeckIds = getAllSubDeckIds(currentDeckId)
            println("  子deckId集合: $subDeckIds")
            val reviewCount = cardDao.getDueReviewCountByDecks(subDeckIds, until)
            println("  统计得到的reviewCount: $reviewCount")
            val deck = allDecks.find { it.deckId == currentDeckId }
            if (deck != null) {
                println("  更新前deck.reviewCount: ${deck.reviewCount}")
                deck.reviewCount = reviewCount
                deckDao.updateDeck(deck)
                println("  已更新deckId $currentDeckId 的reviewCount为: ${deck.reviewCount}")
                currentDeckId = deck.parentId
            } else {
                println("  未找到deckId: $currentDeckId，递归结束")
                break
            }
        }
        println("递归更新reviewCount结束")
    }

    suspend fun getCardDue(cardId: Long): LocalDateTime? {
        return cardDao.getCardById(cardId)?.due
    }
}