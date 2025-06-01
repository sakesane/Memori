package com.example.memori.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardScreen(deckId: String?) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 卡片信息区域（底层）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "卡片界面 DeckId: $deckId")
            // 这里可以展示更多卡片信息
        }
        // 四个按钮区域（高层，底部）
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* 重来 */ }) { Text("重来") }
            Button(onClick = { /* 困难 */ }) { Text("困难") }
            Button(onClick = { /* 一般 */ }) { Text("一般") }
            Button(onClick = { /* 简单 */ }) { Text("简单") }
        }
    }
}
