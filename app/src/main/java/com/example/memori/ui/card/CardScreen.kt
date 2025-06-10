package com.example.memori.ui.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.algorithm.Rating
import com.example.memori.database.util.CustomNewDayTimeConverter
import com.example.memori.ui.card.topInfo.TopInfo
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDateTime

/**
 * 主卡片滑动界面
 * 支持卡片四向滑动，滑动时有动画和方向提示，滑出后自动切换到下一张卡片
 */
@Composable
fun CardScreen(deckId: Long) {
    val vm: CardViewModel = hiltViewModel()
    val context = LocalContext.current
    var cardIndex by remember { mutableStateOf(0) }

    // 是否已翻开（false = 题面，true = 答面）
    var isFlipped by remember { mutableStateOf(false) }

    // 获取卡片队列
    val cards by vm.cardQueue.observeAsState(emptyList())
    var until = LocalDateTime.now()

    // 主题
    val backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
    val cardColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    val darkTheme = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    
    LaunchedEffect(deckId) {
        until = CustomNewDayTimeConverter.getTodayRefreshDateTime(context)
        vm.loadCardQueue(deckId, until)
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            navigationBarContrastEnforced = false,
            darkIcons = !darkTheme
        )
    }

    // 动画状态
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // 弹窗相关状态
    var showTip by remember { mutableStateOf(false) }
    var tipTextRating by remember { mutableStateOf("") }

    // 滑动阈值和滑出距离
    val density = LocalContext.current.resources.displayMetrics.density
    val maxOffsetPx = 32.dp.value * density
    val slideOutDistance = 1500f
    

    Surface(
        modifier = Modifier.fillMaxSize(),
        tonalElevation = 0.dp,
        color = backgroundColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopInfo(deckId = deckId)

            // 队列非空时
            if (cards.isNotEmpty()){
                val currentCard = { 
                    if (cards.isEmpty()) null
                    else cards[(cardIndex) % cards.size]
                }
                val nextCard = cards[(cardIndex + 1) % cards.size]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 16.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 下一张卡片
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                        tonalElevation = 19.dp,
                        color = cardColor
                    ) {
                        CardContent(card = nextCard, isFlipped = false)
                    }
                    // 当前卡片
                    Surface(
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                        color = cardColor,
                        tonalElevation = 20.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = offsetX.value
                                translationY = offsetY.value
                                this.alpha = alpha.value
                                rotationZ = rotation.value
                            }
                            // 手势处理
                            .pointerInput(cardIndex, isFlipped) {
                                var totalDx = 0f
                                var totalDy = 0f
                                if (isFlipped) {
                                    detectDragGestures(
                                        onDragStart = {
                                            totalDx = 0f
                                            totalDy = 0f
                                        },
                                        // 处理拖动事件
                                        onDrag = { _, dragAmount ->
                                            totalDx += dragAmount.x
                                            totalDy += dragAmount.y
                                            if (abs(totalDx) > abs(totalDy)) {
                                                totalDy = 0f
                                                totalDx = totalDx.coerceIn(-maxOffsetPx, maxOffsetPx)
                                            } else {
                                                totalDx = 0f
                                                totalDy = totalDy.coerceIn(-maxOffsetPx, maxOffsetPx)
                                            }
                                            scope.launch {
                                                offsetX.snapTo(totalDx)
                                                offsetY.snapTo(totalDy)
                                                rotation.snapTo(totalDx / 20f)
                                            }
                                        },
                                        // 处理拖动结束事件
                                        onDragEnd = {
                                            val direction = when {
                                                // left, up, right, down
                                                totalDx <= -maxOffsetPx -> "left"
                                                totalDy <= -maxOffsetPx -> "up"
                                                totalDx >= maxOffsetPx -> "right"
                                                totalDy >= maxOffsetPx -> "down"
                                                else -> null
                                            }
                                            // 方向与Rating的映射
                                            val rating = when (direction) {
                                                "up" -> Rating.Again
                                                "left" -> Rating.Hard
                                                "down" -> Rating.Good
                                                "right" -> Rating.Easy
                                                else -> null
                                            }
                                            tipTextRating = rating.toString()
                                            // 调用评分反馈
                                            if (rating != null) {
                                                scope.launch {
                                                    currentCard()?.let { vm.onCardRated(it, rating, until) }
                                                }
                                            }
                                            if (direction != null) {
                                                showTip = true
                                                scope.launch {
                                                    kotlinx.coroutines.delay(400)
                                                    showTip = false
                                                }
                                                isFlipped = false
                                                // 滑走动画+渐隐，切换到新卡片
                                                scope.launch {
                                                    val (targetX, targetY) = when {
                                                        totalDx >= maxOffsetPx -> slideOutDistance to 0f
                                                        totalDx <= -maxOffsetPx -> -slideOutDistance to 0f
                                                        totalDy >= maxOffsetPx -> 0f to slideOutDistance
                                                        totalDy <= -maxOffsetPx -> 0f to -slideOutDistance
                                                        else -> 0f to 0f
                                                    }
                                                    if (targetX != 0f) {
                                                        launch { offsetX.animateTo(targetX, animationSpec = tween(200)) }
                                                        launch { offsetY.animateTo(0f, animationSpec = tween(200)) }
                                                    } else if (targetY != 0f) {
                                                        launch { offsetY.animateTo(targetY, animationSpec = tween(200)) }
                                                        launch { offsetX.animateTo(0f, animationSpec = tween(200)) }
                                                    }
                                                    alpha.animateTo(0f, animationSpec = tween(200))
                                                    // 切换到新卡片
                                                    offsetX.snapTo(0f)
                                                    offsetY.snapTo(0f)
                                                    rotation.snapTo(0f)
                                                    alpha.snapTo(1f)
                                                    if (cards.isNotEmpty()) {
                                                        cardIndex = (cardIndex + 1) % cards.size
                                                    }
                                                }
                                            } else {
                                                // 未达到阈值，回弹动画
                                                scope.launch {
                                                    offsetX.animateTo(0f, animationSpec = tween(100))
                                                    offsetY.animateTo(0f, animationSpec = tween(100))
                                                    rotation.animateTo(0f, animationSpec = tween(100))
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            // 点击事件处理
                            .clickable(
                                enabled = !isFlipped, // 只允许未翻开时点击
                                onClick = { isFlipped = true }
                            )
                    ) {
                        currentCard()?.let { CardContent(card = it, isFlipped = isFlipped) }
                    }
                    SwipeTipDialog(visible = showTip, rating = tipTextRating)
                }
            } else {
                // card 队列为空
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 16.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                        tonalElevation = 20.dp,
                        color = cardColor
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("今日本卡组学习任务已完成")
                        }
                    }
                }
            }
        }
    }
}

