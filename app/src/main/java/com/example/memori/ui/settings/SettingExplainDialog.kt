package com.example.memori.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val explainRowFontSize = 14.sp
// 详情窗口
@Composable
fun SettingExplainDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
        text = { Text(content, fontSize = 16.sp, lineHeight = 24.sp) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

// 说明行
@Composable
fun SettingExplainRow(
    text: String,
    dialogTitle: String,
    dialogContent: String,
    onShowDialog: (String, String) -> Unit
) {
    Row(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = explainRowFontSize,
            modifier = androidx.compose.ui.Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )
        androidx.compose.material3.IconButton(onClick = { onShowDialog(dialogTitle, dialogContent) }) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = "展开说明"
            )
        }
    }
}