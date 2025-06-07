package com.example.memori.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.memori.ui.home.HomeContent
import com.example.memori.ui.smart.SmartContent
import com.example.memori.ui.stats.StatsContent
import com.example.memori.ui.card.CardScreen

object Routes {
    const val HOME = "home"
    const val STATS = "stats"
    const val SMART = "smart"
    const val CARD = "card"
    const val CARD_WITH_ARG = "card/{deckId}"
}

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String = "home") {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) { HomeContent(navController) }
        composable(Routes.STATS) { StatsContent(navController) }
        composable(Routes.SMART) { SmartContent(navController) }
        composable(
            Routes.CARD_WITH_ARG,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType })
        ) { backStackEntry ->
            CardScreen(deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L)
        }
    }
}