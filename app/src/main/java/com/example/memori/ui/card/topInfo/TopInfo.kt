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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopInfo() {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    // 主题
    val outsideColor =  androidx.compose.material3.MaterialTheme.colorScheme.surface
    val numberColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val barColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .padding(
                top = statusBarHeight + 8.dp, // 状态栏高度+自定义间距
                start = 24.dp,
                end = 24.dp
            )
            .fillMaxWidth()
            .height(140.dp),
        color = outsideColor,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
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
                        .weight(2f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "左侧内容",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight(),
                    color = barColor,
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium
                ){}
                Spacer(modifier = Modifier.width(4.dp))
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
                            .padding(3.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "预计",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "20",
                                color = numberColor,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 40.sp
                            )
                            Text(
                                text = "d",
                                color = numberColor,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 26.sp, // 更小的字号
                                modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                            )
                        }
                        Text(
                            text = "学完新词",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TopInfoPreview() {
    TopInfo()
}