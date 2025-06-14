package com.example.memori.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memori.database.entity.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit,
    onDelete: () -> Unit
) {
    var deckName by remember { mutableStateOf(deck.name) }
    var newCardLimitInput by remember { mutableStateOf(deck.newCardLimit?.toString() ?: "5") }
    var newCardLimitError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除该卡组吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                    onDismiss()
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
            TextButton(onClick = { showDeleteConfirm = false }) {
                Text("取消")
            }
        }
        )
    }

    if (!showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Column {
                    Text("卡组编辑", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        thickness = 3.dp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = deckName,
                        onValueChange = { deckName = it },
                        label = { Text("卡组名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCardLimitInput,
                        onValueChange = {
                            newCardLimitInput = it
                            newCardLimitError = null
                        },
                        label = { Text("每日新卡上限") },
                        isError = newCardLimitError != null,
                        supportingText = {
                            if (newCardLimitError != null)
                                Text(newCardLimitError!!, color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showDeleteConfirm = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("删除卡组", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 18.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = newCardLimitInput.toIntOrNull()
                        if (deckName.isBlank()) return@Button
                        if (limit == null || limit < 1) {
                            newCardLimitError = "请输入大于0的整数"
                            return@Button
                        }
                        onConfirm(deckName, limit)
                        onDismiss()
                    },
                    enabled = deckName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("确认", fontSize = 18.sp)
                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 0.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}