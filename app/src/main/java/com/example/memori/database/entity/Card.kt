package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

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
    val deckId: Long,                   // 关联的卡组ID
    val due: Date,
    val stability: Double,
    val difficulty: Double,
    val elapsedDays: Double,
    val scheduledDays: Double,
    val reps: Int,
    val lapses: Int,
    val status: String,
    val lastReview: Date
)