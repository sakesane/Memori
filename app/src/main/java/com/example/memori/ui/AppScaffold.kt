package com.example.memori.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.memori.navigation.AppNavGraph
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import com.example.memori.navigation.Routes

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun AppScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = when (currentRoute) {
        Routes.MAIN -> 0
        Routes.SMART -> 1
        Routes.STATS -> 2
        else -> 0 // 默认主页面
    }
    val isCardScreen = currentRoute?.startsWith("card") == true

    if(isCardScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(navController = navController)
        }
    } else {
        Scaffold(
            topBar = { TopNavBar() },
            bottomBar = { BottomNavBar(selectedIndex = selectedIndex, navController = navController) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Crossfade(
                        targetState = currentRoute,
                        animationSpec = tween(durationMillis = 0)
                    ) { route ->
                        AppNavGraph(navController = navController)
                    }
            }
        }
    }
}