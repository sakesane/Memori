package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "decks",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["deckId"],
        childColumns = ["parentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["parentId"])]
)
data class Deck(
    @PrimaryKey(autoGenerate = true) val deckId: Long = 0,
    val name: String,
    val newCount: Int,
    val reviewCount: Int,
    val newCardLimit: Int = 10,
    val parentId: Long? = null // 顶级卡组为 null，子卡组指向父卡组 id
)