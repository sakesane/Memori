package com.example.memoriuitest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.memoriuitest.ui.MainContent
import com.example.memoriuitest.ui.SmartContent
import com.example.memoriuitest.ui.StatsContent

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String = "main") {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("main") { MainContent(navController) }
        composable("stats") { StatsContent(navController) }
        composable("smart") { SmartContent(navController) }
    }
}