package com.example.memori.settings

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.memori.MemoriApplication.Companion.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object GlobalSettings {

    private val NEW_DAY_BEGIN = intPreferencesKey("refresh_hour")

    private val FSRS_REQUEST_RETENTION = doublePreferencesKey("fsrs_request_retention")
    private val FSRS_MAXIMUM_INTERVAL = doublePreferencesKey("fsrs_maximum_interval")

    fun getRefreshTime(context: Context): Flow<Int> =
        context.settingsDataStore.data.map { it[NEW_DAY_BEGIN] ?: 2 }

    suspend fun setRefreshTime(context: Context, time: Int) {
        context.settingsDataStore.edit {
            it[NEW_DAY_BEGIN] = time
        }
    }

    fun getFsrsRequestRetention(context: Context): Flow<Double> =
        context.settingsDataStore.data.map { it[FSRS_REQUEST_RETENTION] ?: 0.9 }

    suspend fun setFsrsRequestRetention(context: Context, value: Double) {
        context.settingsDataStore.edit { it[FSRS_REQUEST_RETENTION] = value }
    }

    fun getFsrsMaximumInterval(context: Context): Flow<Double> =
        context.settingsDataStore.data.map { it[FSRS_MAXIMUM_INTERVAL] ?: 36500.0 }

    suspend fun setFsrsMaximumInterval(context: Context, value: Double) {
        context.settingsDataStore.edit { it[FSRS_MAXIMUM_INTERVAL] = value }
    }
}