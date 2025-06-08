package com.example.memori.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.memori.database.dao.*
import com.example.memori.database.entity.*
import com.example.memori.database.util.Converters

@Database(
    entities = [
        Card::class,
        Deck::class,
        Revlog::class,
    ],
    version = 3,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class MemoriDB : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun deckDao(): DeckDao
    abstract fun revlogDao(): RevlogDao
}