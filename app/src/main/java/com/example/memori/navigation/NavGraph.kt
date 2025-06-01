package com.example.memori.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.memori.ui.MainContent
import com.example.memori.ui.SmartContent
import com.example.memori.ui.StatsContent
import com.example.memori.ui.CardScreen

object Routes {
    const val MAIN = "main"
    const val STATS = "stats"
    const val SMART = "smart"
    const val CARD = "card"
    const val CARD_WITH_ARG = "card/{deckId}"
}

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String = "main") {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.MAIN) { MainContent(navController) }
        composable(Routes.STATS) { StatsContent(navController) }
        composable(Routes.SMART) { SmartContent(navController) }
        composable(
            Routes.CARD_WITH_ARG,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            CardScreen(deckId = backStackEntry.arguments?.getString("deckId"))
        }
    }
}