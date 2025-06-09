package com.example.memori.database.util

import android.content.Context
import com.example.memori.settings.GlobalSettings
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalTime

object CustomNewDayTimeConverter {
    /**
     * 获取“新的一天”对应的 LocalDateTime
     * @param context 用于读取全局设置
     */
    suspend fun getTodayRefreshDateTime(context: Context): LocalDateTime {
        val time = GlobalSettings.getRefreshTime(context).first()
        val now = LocalDateTime.now()
        val baseDate = now.toLocalDate().plusDays(1)
        return LocalDateTime.of(baseDate, LocalTime.of(time, 0))
    }
}