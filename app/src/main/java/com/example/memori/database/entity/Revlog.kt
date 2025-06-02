package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "revlog",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = ["cardId"],
        childColumns = ["cardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["cardId"])]
)
data class Revlog(
    @PrimaryKey(autoGenerate = true) val revlogId: Long = 0,
    val cardId: Long,      // 关联的卡片ID
    val reviewTime: Long,  // 复习时间（时间戳）
    val ease: Int,         // 用户选择的记忆强度（如1-4）
    val interval: Int,     // 复习后新的间隔天数
    val lastInterval: Int, // 复习前的间隔天数
    val type: Int,         // 复习类型（新卡、复习、再学习等）
    val takenMillis: Int   // 用户复习这张卡片花费的时间（毫秒）
)