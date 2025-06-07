package com.example.memori.ui.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.memori.database.entity.Card

/**
 * 卡片内容组件，显示卡片的 DeckId、内容文本
 */
@Composable
fun CardContent(card: Card, isFlipped: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFlipped) {
            card.front?.let { Text(text = it) } // 面内容
        } else {
            card.back?.let { Text(text = it) } // 背面内容
            card.example?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it)
            }
        }
    }
}