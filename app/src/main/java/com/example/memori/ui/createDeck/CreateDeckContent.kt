package com.example.memori.ui.createDeck

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memori.database.entity.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckContent(
    viewModel: CreateDeckViewModel = hiltViewModel(),
    onDeckCreated: () -> Unit
) {
    var deckName by remember { mutableStateOf("") }
    var deckDesc by remember { mutableStateOf("") }
    var newCardLimitInput by remember { mutableStateOf("5") }
    var newCardLimitError by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    // 获取所有卡组用于父卡组选项
    val allDecks by viewModel.allDecks.collectAsState(initial = emptyList())
    var parentDeckExpanded by remember { mutableStateOf(false) }
    var selectedParentDeck by remember { mutableStateOf<Deck?>(null) }

    val titleFontSize = 32.sp
    val sectionTitleFontSize = 18.sp

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            "创建新卡组",
            fontWeight = FontWeight.ExtraBold,
            fontSize = titleFontSize,
        )
        Divider(thickness = 4.dp)

        // 卡组名称输入框
        OutlinedTextField(
            value = deckName,
            onValueChange = { deckName = it },
            label = { Text("卡组名称") },
            isError = uiState.error.isNotBlank() && deckName.isBlank(),
            supportingText = {
                if (uiState.error.isNotBlank() && deckName.isBlank())
                    Text("卡组名称不能为空", color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth()
        )

        // newCardLimit 输入框
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

        // 父卡组选择
        ExposedDropdownMenuBox(
            expanded = parentDeckExpanded,
            onExpandedChange = { parentDeckExpanded = !parentDeckExpanded }
        ) {
            OutlinedTextField(
                value = selectedParentDeck?.name ?: "无（顶级卡组）",
                onValueChange = {},
                readOnly = true,
                label = { Text("所属父卡组") },
                supportingText = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = parentDeckExpanded,
                onDismissRequest = { parentDeckExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("无（顶级卡组）") },
                    onClick = {
                        selectedParentDeck = null
                        parentDeckExpanded = false
                    }
                )
                allDecks.forEach { deck ->
                    DropdownMenuItem(
                        text = { Text(deck.name) },
                        onClick = {
                            selectedParentDeck = deck
                            parentDeckExpanded = false
                        }
                    )
                }
            }
        }

        // 占位符将按钮推到底部
        Spacer(modifier = Modifier.weight(1f, fill = true))

        Button(
            onClick = {
                val limit = newCardLimitInput.toIntOrNull()
                if (deckName.isBlank()) {
                    newCardLimitError = null
                    viewModel.createDeck(
                        name = deckName,
                        newCardLimit = limit ?: 5,
                        parentId = selectedParentDeck?.deckId
                    ) { onDeckCreated() }
                    return@Button
                }
                if (limit == null || limit < 1) {
                    newCardLimitError = "请输入大于0的整数"
                    return@Button
                }
                viewModel.createDeck(
                    name = deckName,
                    newCardLimit = limit,
                    parentId = selectedParentDeck?.deckId
                ) {
                    onDeckCreated()
                }
            },
            enabled = deckName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp) // 让按钮更高
        ) {
            Text("创建", fontSize = 18.sp) // 让文字更大
        }
        if (uiState.error.isNotBlank()) {
            Text(uiState.error, color = MaterialTheme.colorScheme.error)
        }
    }
}