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
    @PrimaryKey(autoGenerate = true) val cardId: Long = 0,
    val deckId: Long,
    val due: LocalDateTime = LocalDateTime.now(),
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val elapsedDays: Double = 0.0,
    val scheduledDays: Double = 0.0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val status: String = "New",
    val lastReview: LocalDateTime = LocalDateTime.now(),
    // 为了防止mapper报错，设置为可空
    val front: String? = null, // 翻转前
    val back: String? = null,  // 翻转后
    val example: String? = null
)