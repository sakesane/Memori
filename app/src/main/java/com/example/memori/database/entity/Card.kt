package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val deckId: Long,      // 所属牌组
    val front: String,     // 正面内容
    val back: String,      // 背面内容
    val due: Long,         // 下次复习时间（时间戳）
    val interval: Int,     // 间隔天数
    val ease: Int,         // 记忆强度
    val reps: Int,         // 复习次数
    val lapses: Int,       // 失败次数
    val type: Int          // 卡片类型（新卡、复习卡等）
)