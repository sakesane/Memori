package com.example.memori.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memori.database.MemoriDB
import com.example.memori.database.entity.Deck
import com.example.memori.database.entity.Card
import com.example.memori.database.util.DeckResetHelper
import com.example.memori.database.util.CardImportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: MemoriDB,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val deckDao = db.deckDao()
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks

    init {
        viewModelScope.launch {
            // 判断是否已存在 food 卡组，避免重复插入
            val exists = deckDao.getAll().any { it.name == "food" }
            if (false) {
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
                    Card(deckId = fruitDeckId, word = "Apple", definition = "n. 苹果"),
                    Card(deckId = fruitDeckId, word = "Banana", definition = "n. 香蕉"),
                    Card(deckId = fruitDeckId, word = "Orange", definition = "n. 橙子"),
                    Card(deckId = fruitDeckId, word = "Grape", definition = "n. 葡萄"),
                    Card(deckId = fruitDeckId, word = "Watermelon", definition = "n. 西瓜"),
                    Card(deckId = fruitDeckId, word = "Pineapple", definition = "n. 菠萝"),
                    Card(deckId = fruitDeckId, word = "Strawberry", definition = "n. 草莓"),
                    Card(deckId = fruitDeckId, word = "Peach", definition = "n. 桃子"),
                    Card(deckId = fruitDeckId, word = "Cherry", definition = "n. 樱桃"),
                    Card(deckId = fruitDeckId, word = "Mango", definition = "n. 芒果")
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

    fun importFromFile(filePath: String, deckRootName: String, resetDeckNow: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            println("开始导入文件: $filePath")
            val importResult = CardImportUtil.importCardsAndDecksFromFile(filePath)
            val deckDao = db.deckDao()
            val cardDao = db.cardDao()

            // 1. 以文件名（不含扩展名）作为父deck名
            val fileName = File(filePath).nameWithoutExtension
            val parentDeck = Deck(
                name = deckRootName,
                newCount = 0,
                reviewCount = 0,
                newCardLimit = 5,
                parentId = null
            )
            val parentDeckId = deckDao.insert(parentDeck)
            println("插入父Deck: $fileName -> deckId=$parentDeckId")

            // 2. 插入所有wordlist子Deck，parentId为父deck
            val wordListToDeckId: MutableMap<String, Long> = mutableMapOf()
            importResult.decks.forEach { deck ->
                val childDeck = deck.copy(parentId = parentDeckId)
                val deckId = deckDao.insert(childDeck)
                wordListToDeckId[deck.name] = deckId
                println("插入子Deck: ${deck.name} -> deckId=$deckId, parentId=$parentDeckId")
            }

            // 3. 插入所有Card，赋予正确deckId
            importResult.cards.forEach { (card, wordList) ->
                val deckId = wordListToDeckId[wordList]
                if (deckId == null) {
                    println("未找到分组[$wordList]对应的deckId，跳过该卡片: ${card.word}")
                    return@forEach
                }
                val newCard = card.copy(deckId = deckId)
                cardDao.insertCard(newCard)
                println("插入Card: word=${newCard.word}, deckId=$deckId, wordList=$wordList")
            }

            // 4. 强制重置导入的父Deck及所有子Deck
            if (resetDeckNow) {
                val allDeckIds = mutableListOf<Long>()
                allDeckIds.add(parentDeckId)
                allDeckIds.addAll(wordListToDeckId.values)
                DeckResetHelper.resetDeckNewCountForDecks(deckDao, allDeckIds)
            }

            println("导入完成")
        }
    }
}