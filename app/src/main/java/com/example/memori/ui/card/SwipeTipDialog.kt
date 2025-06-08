package com.example.memori.ui.card

import android.annotation.SuppressLint
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
import java.time.LocalDateTime

@Composable
fun SwipeTipDialog(
    visible: Boolean,
    rating: String,
    due: LocalDateTime
) {
    // 传入目标时间，自动与当前时间比较并格式化
    @SuppressLint("DefaultLocale")
    fun formatSchedule(target: LocalDateTime): String {
        val now = LocalDateTime.now()
        println("DEBUG: now = $now, target = $target")
        if (target.isBefore(now)) {
            println("DEBUG: target is before now, return <1分钟")
            return "<1分钟"
        } else {
            val duration = java.time.Duration.between(now, target)
            val dDays = duration.toDays()
            val dHours = duration.toHours()
            val dMins = duration.toMinutes()
            println("DEBUG: duration = $duration, dDays = $dDays, dHours = $dHours, dMins = $dMins")
            if (dDays > 1.0) {
                if (dDays < 31) {
                    println("DEBUG: return ${dDays.toInt()}天")
                    return "${dDays.toInt()}天"
                } else {
                    val months = dDays / 30.0
                    println("DEBUG: return %.1f月".format(months))
                    return String.format("%.1f月", months)
                }
            } else {
                if (dHours > 0) {
                    val mins = dMins % 60
                    println("DEBUG: hours > 0, mins = $mins")
                    return if (mins > 0) {
                        "${dHours}小时${mins}分钟"
                    } else {
                        "${dHours}小时"
                    }
                } else if (dMins > 0) {
                    println("DEBUG: only minutes, return ${dMins}分钟")
                    return "${dMins}分钟"
                }
            }
        }
        println("DEBUG: return Error")
        return String.format("Error")
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
                .alpha(0.92f)
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
                    text = formatSchedule(due),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}