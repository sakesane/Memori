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
        val refreshDateTime = CustomNewDayTimeConverter.getTodayRefreshDateTime(context)
        val todayFlag = refreshDateTime.toLocalDate().toString() // 例如 "2024-06-09"

        val prefs = context.settingsDataStore.data.first()
        val lastReset = prefs[LAST_RESET_DATE]

        if (lastReset != todayFlag) {
            // 需要重置
            val decks = deckDao.getAll()
            decks.forEach { deck ->
                deck.newCount = deck.newCardLimit
                deckDao.updateDeck(deck)
            }
            // 更新flag
            context.settingsDataStore.edit { it[LAST_RESET_DATE] = todayFlag }
        }
    }
}