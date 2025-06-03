package com.example.memori.ui.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopInfo() {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Surface(
        modifier = Modifier
            .padding(
                top = statusBarHeight + 8.dp, // 状态栏高度+自定义间距
                start = 24.dp,
                end = 24.dp
            )
            .fillMaxWidth()
            .height(100.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "顶部圆角矩形",
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}