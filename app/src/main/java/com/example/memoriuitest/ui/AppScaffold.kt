package com.example.memoriuitest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.memoriuitest.navigation.AppNavGraph

@Composable
fun AppScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = when (currentRoute) {
        "main" -> 0
        "smart" -> 1
        "stats" -> 2
        else -> 0 // 默认主页面
    }
    val hideBars = currentRoute?.startsWith("card") == true

    Scaffold(
        topBar = { if (!hideBars) TopNavBar() },
        bottomBar = { if (!hideBars) BottomNavBar(selectedIndex, navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(navController = navController)
        }
    }
}