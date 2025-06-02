package com.example.memori.database

import android.content.Context
import androidx.room.Room
import com.example.memori.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val FILE_NAME = "memori.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MemoriDB {
        return Room.databaseBuilder(
            context,
            MemoriDB::class.java,
            FILE_NAME
        ).build()
    }

    @Provides
    fun provideDeckDao(db: MemoriDB): DeckDao = db.deckDao()

    @Provides
    fun provideCardDao(db: MemoriDB): CardDao = db.cardDao()

    @Provides
    fun provideRevlogDao(db: MemoriDB): RevlogDao = db.revlogDao()
}