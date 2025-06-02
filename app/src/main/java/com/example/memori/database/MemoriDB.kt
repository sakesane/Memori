package com.example.memori.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.memori.database.dao.*
import com.example.memori.database.entity.*

@Database(
    entities = [
        Card::class,
        Deck::class,
        Revlog::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class MemoriDB : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun deckDao(): DeckDao
    abstract fun revlogDao(): RevlogDao
}