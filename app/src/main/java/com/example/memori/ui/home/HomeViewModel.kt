package com.example.memori.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import com.example.memori.database.entity.Card
import com.example.memori.database.util.CustomNewDayTimeConverter
import com.example.memori.database.util.DeckResetHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: MemoriDB,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val cardDao = db.cardDao()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    init {
        viewModelScope.launch {
            // 判断是否已存在 food 卡组，避免重复插入
            val exists = deckDao.getAll().any { it.name == "food" }
            if (!exists) {
                // 1. 插入父卡组 food
                val foodDeckId = db.deckDao().insert(
                    Deck(
                        name = "food",
                        newCardLimit = 5,
                        parentId = null
                    )
                )

                // 2. 插入子卡组 fruit
                val fruitDeckId = db.deckDao().insert(
                    Deck(
                        name = "fruit",
                        newCardLimit = 5,
                        parentId = foodDeckId
                    )
                )

                // 3. 插入10张示例卡片到 fruit
                val exampleCards = listOf(
                    Card(deckId = fruitDeckId, front = "Apple", back = "苹果"),
                    Card(deckId = fruitDeckId, front = "Banana", back = "香蕉"),
                    Card(deckId = fruitDeckId, front = "Orange", back = "橙子"),
                    Card(deckId = fruitDeckId, front = "Grape", back = "葡萄"),
                    Card(deckId = fruitDeckId, front = "Watermelon", back = "西瓜"),
                    Card(deckId = fruitDeckId, front = "Pineapple", back = "菠萝"),
                    Card(deckId = fruitDeckId, front = "Strawberry", back = "草莓"),
                    Card(deckId = fruitDeckId, front = "Peach", back = "桃子"),
                    Card(deckId = fruitDeckId, front = "Cherry", back = "樱桃"),
                    Card(deckId = fruitDeckId, front = "Mango", back = "芒果")
                )
                exampleCards.forEach { db.cardDao().insertCard(it) }
            }
            DeckResetHelper.resetDeckNewCountIfNeeded(context, deckDao)
        }
        viewModelScope.launch {
            deckDao.getAllFlow().collect { deckList ->
                _decks.value = deckList
            }
        }
    }
}