package com.example.memori.util

import com.example.memori.database.entity.Card
import com.example.memori.database.entity.Deck
import java.io.File
import java.time.LocalDateTime

data class ImportResult(
    val decks: List<Deck>,
    val cards: List<Card>
)

object CardImportUtil {

    /**
     * 解析 wordlist 字段，如 "Word List 01"
     */
    private fun parseWordListKey(wordListField: String): String {
        return wordListField.trim()
    }

    /**
     * 导入卡片和卡组，自动建立映射关系
     */
    fun importCardsAndDecksFromFile(filePath: String): ImportResult {
        val lines = File(filePath).readLines()
        // 1. 收集所有 wordlist
        val wordListSet = lines.mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size >= 8) parseWordListKey(parts.last()) else null
        }.distinct()

        // 2. 为每个 wordlist 创建 Deck，自动命名
        val deckMap = mutableMapOf<String, Long>()
        val decks = wordListSet.mapIndexed { idx, wordList ->
            val deckId = idx + 1L
            deckMap[wordList] = deckId
            Deck(
                deckId = deckId,
                name = "deck%02d".format(deckId),
                newCount = 0,
                reviewCount = 0,
                newCardLimit = 5,
                parentId = null
            )
        }

        // 3. 生成 Card，deckId 用映射表
        val cards = lines.mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size >= 8) {
                val wordList = parseWordListKey(parts.last())
                val deckId = deckMap[wordList] ?: return@mapNotNull null
                Card(
                    deckId = deckId,
                    word = parts[0],
                    IPA = parts[1],
                    definition = parts[2],
                    exampleEN = parts[3],
                    exampleCN = parts[4],
                    mnemonic = parts[5],
                    add = parts[6],
                    due = LocalDateTime.now(),
                    stability = 0.0,
                    difficulty = 0.0,
                    elapsedDays = 0.0,
                    scheduledDays = 0.0,
                    reps = 0,
                    lapses = 0,
                    status = "New",
                    lastReview = LocalDateTime.now()
                )
            } else null
        }

        return ImportResult(decks, cards)
    }
}