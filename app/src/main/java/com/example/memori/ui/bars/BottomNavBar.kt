package com.example.memori.ui.bars

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    selectedIndex: Int, // 传入当前页面，保证点击时不会重复导航
    navController: NavController
) {
    val routes = listOf("main", "smart", "stats")
    NavigationBar {
        routes.forEachIndexed { index, route ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = {
                    if (index != selectedIndex) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                },
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Default.Home, contentDescription = "主页面")
                        1 -> Icon(Icons.Default.Star, contentDescription = "智能")
                        2 -> Icon(
                            painter = painterResource(id = com.example.memori.R.drawable.bar_chart_24),
                            contentDescription = "统计"
                        )
                        else -> Spacer(modifier = Modifier.size(24.dp))
                    }
                },
                label = null
            )
        }
    }
}