package com.example.memori.ui.card

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.database.entity.Card

/**
 * 卡片内容组件，显示卡片的 DeckId、内容文本
 */
@Composable
fun CardContent(card: Card, isFlipped: Boolean) {

    // 主题
    val iconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    val circleColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val charColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
    val questionColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
    val answerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部一行：左-圆形首字母，右-图标
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左上角实心圆+首字符
            val firstChar = card.front?.firstOrNull()?.toString() ?: ""
            Box(
                modifier = Modifier
                    .size(38.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize(),
                    onDraw = {
                        drawCircle(
                            color = circleColor
                        )
                    }
                )
                Text(
                    text = firstChar,
                    color = charColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            // 右上角图标
            Icon(
                painter = painterResource(id = com.example.memori.R.drawable.asterisk_24dp),
                contentDescription = "icon",
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // 大号加粗的 card.front
            card.front?.let {
                Text(
                    text = it,
                    color = questionColor,
                    fontSize = 44.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (isFlipped) {
                card.back?.let {
                    Text(
                        text = it,
                        fontSize = 32.sp,
                        color = answerColor
                    )
                }
                card.example?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        fontSize = 24.sp
                    )
                }
            }
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun CardContentPreview1() {
    CardContent( Card(deckId = 0, front = "Apple", back = "苹果"), true)
}
