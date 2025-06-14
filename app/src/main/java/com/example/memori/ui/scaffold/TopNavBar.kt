package com.example.memori.ui.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TopNavBar(navController: NavController) {
    val insets = WindowInsets.statusBars.asPaddingValues()
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val barColor = MaterialTheme.colorScheme.surfaceContainer

    Surface(
        color = barColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .padding(top = insets.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Logo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    IconButton(onClick = {
                        if (currentRoute == "settings") {
                            navController.popBackStack()
                        } else {
                            navController.navigate("settings")
                        }
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            }
        }
    }
}