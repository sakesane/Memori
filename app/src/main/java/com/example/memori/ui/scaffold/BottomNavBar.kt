package com.example.memori.ui.scaffold

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.memori.R

@Composable
fun BottomNavBar(
    selectedIndex: Int, // 传入当前页面，保证点击时不会重复导航
    navController: NavController
) {
    val routes = listOf("home", "smart", "stats")
    val labels = listOf("主页", "AI", "统计")
    val barColor = MaterialTheme.colorScheme.secondaryContainer
    val iconColor = MaterialTheme.colorScheme.onSurface
    
    NavigationBar(
        modifier = Modifier.height(96.dp),
        containerColor = barColor
    ) {
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
                        0 -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "主页面",
                            tint = iconColor
                        )
                        1 -> Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "智能",
                            tint = iconColor
                        )
                        2 -> Icon(
                            painter = painterResource(id = R.drawable.bar_chart_24),
                            contentDescription = "统计",
                            tint = iconColor
                        )
                        else -> Spacer(modifier = Modifier.size(24.dp))
                    }
                },
                label = {
                    Text(
                        text = labels[index],
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
            )
        }
    }
}