package com.example.memori

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MemoriApplication : Application(){
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        // val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }
}