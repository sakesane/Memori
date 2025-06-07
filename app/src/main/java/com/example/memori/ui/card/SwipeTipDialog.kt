package com.example.memori.ui.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SwipeTipDialog(
    visible: Boolean,
    rating: String,
    schedule: Double
) {
    // 时间格式化
    fun formatSchedule(days: Double): String {
        return when {
            days < 1.0 -> {
                val totalMinutes = (days * 24 * 60).toInt()
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                when {
                    hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟"
                    hours > 0 -> "${hours}小时"
                    else -> "${minutes}分钟"
                }
            }
            days < 30 -> "${days.toInt()}天"
            else -> String.format("%.1f月", days / 30.0)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .width(152.dp)
                .height(80.dp)
                .alpha(0.8f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = rating,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatSchedule(schedule),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}