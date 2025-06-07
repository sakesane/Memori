package com.example.memori.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

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
    val rating: String,
    val elapsedDays: Double,        // 距离上次复习经过的天数
    val scheduledDays: Double,      // 计划间隔天数
    val review: LocalDateTime,       // 复习时间
    val status: String          // 当前状态
)