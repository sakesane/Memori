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
     * 获取指定deck下的复习卡片和新卡片队列
     * @param deckId 目标卡组ID
     * @param until 截止时间（应传入当天时间：由用户定义的每日刷新时间决定）
     */
    fun loadCardQueue(deckId: Long, until: LocalDateTime) {
        viewModelScope.launch {
            // 1. 获取deck，读取新卡上限
            val deck = deckDao.getAll().find { it.deckId == deckId }
            val newCardLimit = deck?.newCardLimit ?: 0 // 假设Deck实体有newCardLimit字段

            // 2. 获取复习卡片
            val dueCards = cardDao.getDueCardsByDeck(deckId, until)
            // 3. 获取新卡片
            val newCards = cardDao.getNewCardsByDeck(deckId, newCardLimit)

            // 4. 合并2、3卡片队列（可自定义混排逻辑）
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
    fun onCardRated(card: Card, rating: Rating) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. 转换为FSRSCard
            val fsrsCard = card.toFSRSCard()
            val now = LocalDateTime.now()
            // 2. 调用FSRS.repeat
            val fsrs = FSRS()
            val schedulingMap = fsrs.repeat(fsrsCard, now)
            // 3. 获取用户评分对应的SchedulingInfo
            val schedulingInfo = schedulingMap[rating] ?: return@launch
            val updatedFSRSCard = schedulingInfo.card
            // 4. 转回Card，保留原有内容字段
            val updatedCard = updatedFSRSCard.toCard(
                cardId = card.cardId,
                deckId = card.deckId
            ).copy(
                front = card.front,
                back = card.back,
                example = card.example
            )
            // 5. 更新数据库
            cardDao.updateCard(updatedCard)
            // 6. 可选：刷新队列
            loadCardQueue(card.deckId, LocalDateTime.now())
        }
    }
}