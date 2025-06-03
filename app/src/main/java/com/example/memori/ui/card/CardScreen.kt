package com.example.memori.ui.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
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

/**
 * 主卡片滑动界面
 * 支持卡片四向滑动，滑动时有动画和方向提示，滑出后自动切换到下一张卡片
 */
@Composable
fun CardScreen(deckId: String?) {
    val context = LocalContext.current
    var cardIndex by remember { mutableStateOf(0) }
    val cards = listOf("卡片A", "卡片B", "卡片C")

    // 动画状态
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // 弹窗相关状态
    var showTip by remember { mutableStateOf(false) }
    var tipText by remember { mutableStateOf("") }

    // 滑动阈值和滑出距离
    val density = LocalContext.current.resources.displayMetrics.density
    val maxOffsetPx = 32.dp.value * density
    val slideOutDistance = 1500f

    // 新卡片渐变动画相关变量
    var showNewCard by remember { mutableStateOf(false) }
    val newCardAlpha = remember { Animatable(0f) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopInfo()
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
                // 下一张卡片（z轴下方）
                val nextIndex = (cardIndex + 1) % cards.size
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    tonalElevation = 19.dp
                ) {
                    CardContent(deckId = deckId, text = cards[nextIndex])
                }
                // 当前卡片（z轴上方，带动画和手势）
                Surface(
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    tonalElevation = 20.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = offsetX.value
                            translationY = offsetY.value
                            this.alpha = alpha.value
                            rotationZ = rotation.value
                        }
                        .pointerInput(cardIndex) {
                            var totalDx = 0f
                            var totalDy = 0f
                            detectDragGestures(
                                onDragStart = {
                                    totalDx = 0f
                                    totalDy = 0f
                                    // 不再设置dragDirection
                                },
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
                                    // 不再实时显示滑动方向
                                    scope.launch {
                                        offsetX.snapTo(totalDx)
                                        offsetY.snapTo(totalDy)
                                        rotation.snapTo(totalDx / 20f)
                                    }
                                },
                                onDragEnd = {
                                    val direction = when {
                                        totalDx >= maxOffsetPx -> "向右滑动"
                                        totalDx <= -maxOffsetPx -> "向左滑动"
                                        totalDy >= maxOffsetPx -> "向下滑动"
                                        totalDy <= -maxOffsetPx -> "向上滑动"
                                        else -> null
                                    }
                                    if (direction != null) {
                                        // 弹窗提示
                                        tipText = direction
                                        showTip = true
                                        scope.launch {
                                            kotlinx.coroutines.delay(400)
                                            showTip = false
                                        }
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
                                            cardIndex = (cardIndex + 1) % cards.size
                                            // 新卡片渐变出现等...
                                        }
                                    } else {
                                        // 未达到阈值，回弹动画
                                        scope.launch {
                                            offsetX.animateTo(0f, animationSpec = tween(200))
                                            offsetY.animateTo(0f, animationSpec = tween(200))
                                            rotation.animateTo(0f, animationSpec = tween(200))
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    CardContent(deckId = deckId, text = cards[cardIndex])
                }
                SwipeTipDialog(visible = showTip, text = tipText)
            }
        }
    }
}

