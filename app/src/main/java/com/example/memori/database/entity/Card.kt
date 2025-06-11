package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "cards",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["deckId"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["deckId"])]
)
data class Card(
    // 键
    @PrimaryKey(autoGenerate = true) val cardId: Long = 0,
    val deckId: Long,

    // FSRS 算法相关字段
    val due: LocalDateTime = LocalDateTime.now(),
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val elapsedDays: Double = 0.0,
    val scheduledDays: Double = 0.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val status: String = "New",
    val lastReview: LocalDateTime = LocalDateTime.now(),

    // 词条相关字段
    // 为了防止 mapper 报错，设置为可空
    val word: String? = null,
    val IPA: String? = null,
    val definition: String? = null,
    val exampleEN: String? = null,
    val exampleCN: String? = null,
    val mnemonic: String? = null,
    val add: String? = null
)