package com.example.memori.database.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.memori.MemoriApplication.Companion.settingsDataStore
import com.example.memori.database.dao.DeckDao
import kotlinx.coroutines.flow.first

object DeckResetHelper {
    private val LAST_RESET_DATE = stringPreferencesKey("last_deck_newcount_reset_date")

    suspend fun resetDeckNewCountIfNeeded(context: Context, deckDao: DeckDao) {
        /* 
            重置判断条件：lastReset != todayFlag
            lastReset 从 DataStore 中读取
            todayFlag 由 CustomNewDayTimeConverter 获取
         */
        val refreshDateTime = CustomNewDayTimeConverter.getTodayRefreshDateTime(context).minusDays(1)
        val todayFlag = refreshDateTime.toLocalDate().toString()

        val prefs = context.settingsDataStore.data.first()
        val lastReset = prefs[LAST_RESET_DATE]

        if (lastReset != todayFlag) {
            // 重置内容逻辑
            val decks = deckDao.getAll()
            decks.forEach { deck ->
                deck.newCount = deck.newCardLimit
                deckDao.updateDeck(deck)
            }
            // 更新flag
            context.settingsDataStore.edit { it[LAST_RESET_DATE] = todayFlag }
        }
        println("调用了 DeckResetHelper. Last reset date: $lastReset, Today: $todayFlag")
    }

    suspend fun resetDeckNewCountForDecks(deckDao: DeckDao, deckIds: List<Long>) {
        val decks = deckDao.getDecksByIds(deckIds)
        decks.forEach { deck ->
            deck.newCount = deck.newCardLimit
            deckDao.updateDeck(deck)
        }
        println("强制重置了指定 Deck: $deckIds 的 newCount")
    }

    suspend fun updateDeckNewCountAfterLimitChanged(deckDao: DeckDao, deckId: Long, oldLimit: Int, newLimit: Int) {
        val deck = deckDao.getDeckById(deckId) ?: return
        val originCount = deck.newCount ?: 0
        val diff = newLimit - oldLimit
        deck.newCount = (originCount + diff).coerceAtLeast(0)
        deckDao.updateDeck(deck)
        println("卡组 $deckId 新卡上限变更，newCount 已调整为 ${deck.newCount}")
    }
}