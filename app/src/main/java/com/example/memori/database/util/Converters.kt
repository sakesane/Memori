package com.example.memori.database.util

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

// 时间数据格式转换器
class Converters {
    @TypeConverter
    fun toLocalDataTime(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC) }
    }

    @TypeConverter
    fun fromLocalDataTime(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? {
        return value?.toEpochDay()
    }

    @TypeConverter
    fun toLocalData(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(value) }
    }
}