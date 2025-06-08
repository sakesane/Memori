package com.example.memori.settings

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.memori.MemoriApplication.Companion.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object GlobalSettings {

    private val NEW_DAY_BEGIN = intPreferencesKey("refresh_hour")

    fun getRefreshTime(context: Context): Flow<Int> =
        context.settingsDataStore.data.map { it[NEW_DAY_BEGIN] ?: 0 }

    suspend fun setRefreshTime(context: Context, time: Int) {
        context.settingsDataStore.edit {
            it[NEW_DAY_BEGIN] = time
        }
    }
}