package com.example.memoriuitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.memoriuitest.ui.theme.MemoriUITestTheme
import androidx.navigation.compose.rememberNavController
import com.example.memoriuitest.ui.AppScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoriUITestTheme {
                val navController = rememberNavController()
                AppScaffold(navController)
            }
        }
    }
}