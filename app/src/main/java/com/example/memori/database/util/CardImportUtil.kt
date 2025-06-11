package com.example.memori.database.util

import com.example.memori.database.entity.Card
import com.example.memori.database.entity.Deck
import java.io.File
import java.time.LocalDateTime

data class ImportResult(
    val decks: List<Deck>,
    val cards: List<Pair<Card, String>> // Pair<Card, wordList分组名>
)

/**
 * 从指定文件导入卡片，每行格式：
 * word\tIPA\tdefinition\texampleEN\texampleCN\tmnemonic\tadd\tWord List XX
 * 只读取前7个字段，最后一个字段提取deckId
 */
object CardImportUtil {

    // 提取 wordlist 分组名
    private fun parseWordListKey(wordListField: String): String {
        return wordListField.trim()
    }

    /**
     * 读取文件并生成Deck列表和(Card, wordList)对
     */
    fun importCardsAndDecksFromFile(filePath: String): ImportResult {
        println("读取文件: $filePath")
        val lines = File(filePath).readLines()
        println("总行数: ${lines.size}")

        // 1. 收集所有 wordlist 分组
        val wordListSet = lines.mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size >= 8) parseWordListKey(parts.last()) else null
        }.distinct()
        println("检测到分组: $wordListSet")

        // 2. 为每个 wordlist 创建 Deck，命名为 word list /d
        val decks = wordListSet.map { wordList ->
            println("准备创建Deck: $wordList")
            Deck(
                name = wordList, // 不做lowercase，保证一致性
                newCount = 0,
                reviewCount = 0,
                newCardLimit = 5,
                parentId = null
            )
        }

        // 3. 生成 Card，并附带 wordList 分组名
        val cards = lines.mapNotNull { line ->
            val parts = line.split('\t')
            val trimmedParts = parts.filter { it.isNotBlank() }
            println("调试：分割后字段数=${parts.size}，去空后=${trimmedParts.size}，最后字段='${trimmedParts.lastOrNull()}'")
            if (trimmedParts.size >= 7) {
                val wordList = trimmedParts.last()
                val card = Card(
                    deckId = 0L,
                    word = trimmedParts[0],
                    IPA = trimmedParts[1],
                    definition = trimmedParts[2],
                    exampleEN = trimmedParts[3],
                    exampleCN = trimmedParts[4],
                    mnemonic = trimmedParts[5],
                    add = trimmedParts[6]
                )
                println("读取到Card: word=${card.word}, wordList=$wordList")
                card to wordList
            } else {
                println("跳过格式错误行: $line")
                null
            }
        }

        println("共生成Deck: ${decks.size} 个, Card: ${cards.size} 张")
        return ImportResult(decks, cards)
    }
}