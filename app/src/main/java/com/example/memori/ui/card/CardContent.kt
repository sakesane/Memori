package com.example.memori.ui.card

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    val circleColor = MaterialTheme.colorScheme.primary
    val charColor = MaterialTheme.colorScheme.onPrimary
    val questionColor = MaterialTheme.colorScheme.onPrimaryContainer
    val definationColor = MaterialTheme.colorScheme.onPrimaryContainer
    val exampleContainerColor = MaterialTheme.colorScheme.primaryContainer
    val exampleENColor = MaterialTheme.colorScheme.onPrimaryContainer
    val exampleCNColor = MaterialTheme.colorScheme.onPrimaryContainer
    val addContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val addColor = MaterialTheme.colorScheme.onSurfaceVariant


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
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
            val firstChar = card.word?.firstOrNull()?.toString() ?: ""
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
                    fontSize = 24.sp
                )
            }
            // 右上角图标
            Icon(
                painter = painterResource(id = com.example.memori.R.drawable.asterisk_24dp),
                contentDescription = "icon",
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // 大号加粗的 card.word
            card.word?.let {
                Text(
                    text = it,
                    color = questionColor,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                )
                // 音标显示
                if (!card.IPA.isNullOrBlank()) {
                    Text(
                        text = card.IPA!!,
                        color = questionColor.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            // 只有在背面才显示 definition
            if (isFlipped) {
                card.definition?.let {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = definationColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            // 显示 exampleEN、exampleCN
            Surface(
                color = exampleContainerColor,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // 英文例句
                    card.exampleEN?.let {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                            color = exampleENColor,
                            lineHeight = 20.sp,
                            modifier = Modifier.graphicsLayer {
                                alpha = if (isFlipped) 1f else 0.5f
                            }
                        )
                    }
                    // 中文例句
                    if (isFlipped) {
                        card.exampleCN?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Light,
                                color = exampleCNColor
                            )
                        }
                    }
                }
            }

            // 显示 mnemonic、add
            // 只有在背面才显示这些内容
            if (isFlipped && (!card.mnemonic.isNullOrBlank() || !card.add.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = addContainerColor,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        card.mnemonic?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                color = addColor
                            )
                        }
                        card.add?.takeIf { it.isNotBlank() }?.let {
                            if (!card.mnemonic.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                color = addColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardContentPreview1() {
    CardContent( Card(deckId = 0, word = "Apple", definition = "苹果"), true)
}
