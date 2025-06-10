package com.example.memori.ui.card.topInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.memori.database.entity.Deck

@Composable
fun TopInfo(deckId: Long, viewModel: TopInfoViewModel = hiltViewModel()) {
    val deck by viewModel.deckFlow(deckId).collectAsState(initial = null)
    var allNewCount by remember { mutableStateOf(0) }

    LaunchedEffect(deckId) {
        allNewCount = viewModel.getAllNewCount(deckId)
    }

    val expectedDays = getExpectedStudyDays(deck, allNewCount)

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    // 主题
    val outsideColor =  MaterialTheme.colorScheme.surface
    val dayColor = MaterialTheme.colorScheme.outline
    val barColor = MaterialTheme.colorScheme.outlineVariant
    val rTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val newColor = MaterialTheme.colorScheme.error
    val relearnColor = MaterialTheme.colorScheme.primary
    val sumColor = MaterialTheme.colorScheme.tertiary

    Surface(
        modifier = Modifier
            .padding(
                top = statusBarHeight + 8.dp, // 状态栏高度+自定义间距
                start = 24.dp,
                end = 24.dp
            )
            .fillMaxWidth()
            .height(120.dp),
        color = outsideColor,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 20.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp), // 内部padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧内容
                Box(
                    modifier = Modifier
                        .weight(2.1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val newCount = deck?.newCount ?: 0
                        val reviewCount = deck?.reviewCount ?: 0
                        val sumCount = newCount + reviewCount
                        statusCountBox("新词", newCount, newColor)
                        statusCountBox("复习", reviewCount, relearnColor)
                        statusCountBox("共计", sumCount, sumColor)
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(),
                    color = barColor,
                    shape = MaterialTheme.shapes.medium
                ){}
                Spacer(modifier = Modifier.width(8.dp))
                // 右侧内容
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "预计",
                            color = rTextColor
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = expectedDays.toString(),
                                color = dayColor,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 32.sp
                            )
                            Text(
                                text = "d",
                                color = dayColor,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                            )
                        }
                        Text(
                            text = "学完新词",
                            color = rTextColor
                        )
                    }
                }
            }
        }
    }
}

private fun getExpectedStudyDays(deck: Deck?, allNewCount: Int): Int {
    if (deck?.newCardLimit == null || deck.newCardLimit == 0) return 0
    val newCardLimit = deck.newCardLimit ?: 1
    return (allNewCount + newCardLimit - 1) / newCardLimit // 向上取整
}

@Composable
fun statusCountBox(status: String, count: Int, numberColor: androidx.compose.ui.graphics.Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = status,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            color = numberColor,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 32.sp
        )
    }
}