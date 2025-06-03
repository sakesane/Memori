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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

/**
 * 卡片内容组件，显示卡片的 DeckId、内容文本和滑动方向提示
 */
@Composable
fun CardContent(deckId: String?, text: String, dragDirection: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "卡片界面 DeckId: $deckId")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))
        // 如果有滑动方向提示，则显示
        dragDirection?.let {
            Text(
                text = it,
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(top = 16.dp)
            )
        }
    }
}