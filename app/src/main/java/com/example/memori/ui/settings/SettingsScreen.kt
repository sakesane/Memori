package com.example.memori.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.settings.GlobalSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(context: Context) {
    val scope = rememberCoroutineScope()

    val refreshHour by GlobalSettings.getRefreshTime(context).collectAsState(initial = 2)
    val fsrsRequestRetention by GlobalSettings.getFsrsRequestRetention(context).collectAsState(initial = 0.85)
    val fsrsMaximumInterval by GlobalSettings.getFsrsMaximumInterval(context).collectAsState(initial = 36500.0)

    var refreshHourInput by remember { mutableStateOf("") }
    var requestRetentionInput by remember { mutableStateOf("") }
    var maximumIntervalInput by remember { mutableStateOf("") }

    var refreshHourError by remember { mutableStateOf<String?>(null) }
    var requestRetentionError by remember { mutableStateOf<String?>(null) }
    var maximumIntervalError by remember { mutableStateOf<String?>(null) }

    // 展开弹窗状态
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // 主题
    val titleFontSize = 32.sp           // 大标题
    val sectionTitleFontSize = 18.sp    // 标题
    val inputPlaceholderFontSize = 14.sp // 输入框 placeholder
    val fsrsDescFontSize = 12.sp        // FSRS 算法参数说明文字

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            "设置",
            fontWeight = FontWeight.ExtraBold,
            fontSize = titleFontSize, // 使用变量
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(thickness = 4.dp)
        Spacer(Modifier.height(16.dp))

        // 新一天开始时间
        Text(
            "新一天起始时间", 
            fontSize = sectionTitleFontSize, // 使用变量
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = refreshHourInput,
                onValueChange = {
                    refreshHourInput = it
                    refreshHourError = null
                },
                label = { Text("新一天开始时间（小时）") },
                placeholder = { Text("输入 (0, 23) 的数字", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }, // 使用变量
                isError = refreshHourError != null,
                supportingText = { if (refreshHourError != null) Text(refreshHourError!!, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val hour = refreshHourInput.toIntOrNull()
                if (hour == null || hour !in 0..23) {
                    refreshHourError = "请输入 0 到 23 之间的整数"
                } else {
                    scope.launch {
                        GlobalSettings.setRefreshTime(context, hour)
                        refreshHourError = null
                    }
                }
            },
            modifier = Modifier.
                fillMaxHeight()
            ) { Text("应用") }
        }
        SettingExplainRow(
            text = "每天几点后开始新的一天\n当前：新的一天在 $refreshHour : 00 开始",
            dialogTitle = "新一天起始时间说明",
            dialogContent =
            "设置每天从几点开始算作新的一天。\n" +
                    "这个时间影响你的卡组学习队列刷新时间，当前设置为新的一天在 $refreshHour : 00 开始。\n" +
                    "例如设置为2，则凌晨2点后才算作新的一天，适合夜猫子用户。",
            onShowDialog = { title, content ->
                dialogTitle = title
                dialogContent = content
                showDialog = true
            }
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(12.dp))

        // FSRS 算法参数
        Text(
            "FSRS 算法参数",
            fontWeight = FontWeight.Bold,
            fontSize = sectionTitleFontSize, // 使用变量
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "FSRS (Free Spaced Repetition Scheduler)\n是一种基于 DSR (Difficulty, Stability, Retrievability) model 的间隔重复算法，" +
            "通过动态调整复习间隔来提高学习效率。\n以下是一些关键的 FSRS 参数设置。",
            fontSize = fsrsDescFontSize, // 使用变量
            lineHeight = 16.sp
        )

        Spacer(Modifier.height(16.dp))

        // FSRS 期望记忆留存率
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = requestRetentionInput,
                onValueChange = {
                    requestRetentionInput = it
                    requestRetentionError = null
                },
                label = { Text("FSRS 期望记忆留存率") },
                placeholder = { Text("输入 [0, 1] 的小数", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }, // 使用变量
                isError = requestRetentionError != null,
                supportingText = { if (requestRetentionError != null) Text(requestRetentionError!!, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val value = requestRetentionInput.toDoubleOrNull()
                if (value == null || value <= 0.0 || value >= 1.0) {
                    requestRetentionError = "请输入 0 到 1 之间的小数（不含 0 和 1）"
                } else {
                    scope.launch {
                        GlobalSettings.setFsrsRequestRetention(context, value)
                        requestRetentionError = null
                    }
                }
            },
            modifier = Modifier.
                fillMaxHeight()
            ) { Text("应用") }
        }
        SettingExplainRow(
            text = "FSRS 计算下次复习的目标记忆概率\n当前：$fsrsRequestRetention",
            dialogTitle = "期望记忆留存率说明",
            dialogContent =
            "记忆留存率（Request Retention）\n" +
                    "FSRS 算法会根据你设置的期望记忆留存率，动态调整复习间隔。\n" +
                    "默认值为 0.85，即 85% 的记忆留存率。一般建议设置为 0.8 ~ 0.9，数值越高，复习频率越高，遗忘概率越低。\n" +
                    "若设置为 0.9 以上，可能导致复习数量大幅增加，请谨慎选择。",
            onShowDialog = { title, content ->
                dialogTitle = title
                dialogContent = content
                showDialog = true
            }
        )

        Spacer(Modifier.height(16.dp))

        // FSRS 最大重复间隔
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = maximumIntervalInput,
                onValueChange = {
                    maximumIntervalInput = it
                    maximumIntervalError = null
                },
                label = { Text("FSRS 最大重复间隔（天）") },
                placeholder = { Text("请输入 [1, 36500] 之间的数字", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }, // 使用变量
                isError = maximumIntervalError != null,
                supportingText = { if (maximumIntervalError != null) Text(maximumIntervalError!!, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val value = maximumIntervalInput.toDoubleOrNull()
                if (value == null || value < 1.0 || value > 36500.0) {
                    maximumIntervalError = "请输入 1 到 36500 之间的数字"
                } else {
                    scope.launch {
                        GlobalSettings.setFsrsMaximumInterval(context, value)
                        maximumIntervalError = null
                    }
                }
            },
            modifier = Modifier.
                fillMaxHeight()
            ) { Text("应用") }
        }
        SettingExplainRow(
            text = "FSRS 允许的最大复习间隔天数\n当前：$fsrsMaximumInterval 天",
            dialogTitle = "最大重复间隔说明",
            dialogContent =
            "最大重复间隔（Maximum Interval）\n\n" +
                    "重复间隔即为同一张卡片两次出现间隔的时间，FSRS 算法推算的最大复习间隔不会超过该值。\n\n" +
                    "默认36500天（约100年），可根据需要调整。",
            onShowDialog = { title, content ->
                dialogTitle = title
                dialogContent = content
                showDialog = true
            }
        )
    }

    // 弹窗
    if (showDialog) {
        SettingExplainDialog(
            title = dialogTitle,
            content = dialogContent,
            onDismiss = { showDialog = false }
        )
    }
}