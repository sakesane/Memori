package com.example.memori.ui.insertCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertCardContent(
    viewModel: InsertCardViewModel = hiltViewModel(),
    onCardInserted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDeckId by remember { mutableStateOf<Long?>(null) }
    var deckDropdownExpanded by remember { mutableStateOf(false) }
    var word by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var IPA by remember { mutableStateOf("") }
    var exampleEN by remember { mutableStateOf("") }
    var exampleCN by remember { mutableStateOf("") }
    var mnemonic by remember { mutableStateOf("") }
    var add by remember { mutableStateOf("") }

    // 错误提示
    var deckError by remember { mutableStateOf<String?>(null) }
    var wordError by remember { mutableStateOf<String?>(null) }
    var definitionError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val titleFontSize = 32.sp
    val sectionTitleFontSize = 18.sp

    val inputPlaceholderFontSize = 14.sp

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            "插入卡片",
            fontWeight = FontWeight.ExtraBold,
            fontSize = titleFontSize,
        )
        Divider(thickness = 4.dp)

        // 必填项分组
        Text(
            "必填项",
            fontWeight = FontWeight.Bold,
            fontSize = sectionTitleFontSize,
        )

        // 选择卡组
        ExposedDropdownMenuBox(
            expanded = deckDropdownExpanded,
            onExpandedChange = { deckDropdownExpanded = !deckDropdownExpanded }
        ) {
            OutlinedTextField(
                value = uiState.decks.find { it.deckId == selectedDeckId }?.name ?: "",
                onValueChange = {},
                label = { Text("选择所属卡组") },
                placeholder = { Text("选择卡片直属的唯一卡组", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                isError = deckError != null,
                supportingText = { if (deckError != null) Text(deckError!!, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            )
            ExposedDropdownMenu(
                expanded = deckDropdownExpanded,
                onDismissRequest = { deckDropdownExpanded = false }
            ) {
                uiState.decks.forEach { deck ->
                    DropdownMenuItem(
                        text = { Text(deck.name) },
                        onClick = {
                            selectedDeckId = deck.deckId
                            deckDropdownExpanded = false
                            deckError = null
                        }
                    )
                }
            }
        }

        // 单词
        OutlinedTextField(
            value = word,
            onValueChange = {
                word = it
                wordError = null
            },
            label = { Text("单词") },
            placeholder = { Text("将以大字加粗显示在卡片上", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            isError = wordError != null,
            supportingText = { if (wordError != null) Text(wordError!!, color = MaterialTheme.colorScheme.error) },
            modifier = Modifier.fillMaxWidth()
        )

        // 释义
        OutlinedTextField(
            value = definition,
            onValueChange = {
                definition = it
                definitionError = null
            },
            label = { Text("释义") },
            placeholder = { Text("将显示于卡片翻开后，单词下方", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            isError = definitionError != null,
            supportingText = { if (definitionError != null) Text(definitionError!!, color = MaterialTheme.colorScheme.error) },
            modifier = Modifier.fillMaxWidth()
        )

        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        // 选填项折叠
        var optionalExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { optionalExpanded = !optionalExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "选填项",
                fontWeight = FontWeight.Bold,
                fontSize = sectionTitleFontSize,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (optionalExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                contentDescription = if (optionalExpanded) "收起" else "展开",
                modifier = Modifier.rotate(if (optionalExpanded) 180f else 0f)
            )
        }

        AnimatedVisibility(visible = optionalExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = IPA,
                    onValueChange = { IPA = it },
                    label = { Text("音标") },
                    placeholder = { Text("将显示在卡片上，单词下方", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exampleEN,
                    onValueChange = { exampleEN = it },
                    label = { Text("英文例句") },
                    placeholder = { Text("将显示于卡片翻开前", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = exampleCN,
                    onValueChange = { exampleCN = it },
                    label = { Text("中文例句") },
                    placeholder = { Text("将显示于卡片翻开后，英文例句下方", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mnemonic,
                    onValueChange = { mnemonic = it },
                    label = { Text("助记") },
                    placeholder = { Text("将显示于卡片翻开后，例句下方", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = add,
                    onValueChange = { add = it },
                    label = { Text("补充") },
                    placeholder = { Text("将显示于卡片翻开后，助记下方", fontSize = inputPlaceholderFontSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // 校验
                var hasError = false
                if (selectedDeckId == null) {
                    deckError = "请选择卡组"
                    hasError = true
                }
                if (word.isBlank()) {
                    wordError = "单词不能为空"
                    hasError = true
                }
                if (definition.isBlank()) {
                    definitionError = "释义不能为空"
                    hasError = true
                }
                if (hasError) return@Button

                selectedDeckId?.let {
                    viewModel.insertCard(
                        deckId = it,
                        word = word,
                        IPA = IPA,
                        definition = definition,
                        exampleEN = exampleEN,
                        exampleCN = exampleCN,
                        mnemonic = mnemonic,
                        add = add
                    ) {
                        onCardInserted()
                    }
                }
            },
            enabled = selectedDeckId != null && word.isNotBlank() && definition.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("插入", fontSize = 20.sp)
        }
        if (uiState.error.isNotBlank()) {
            Text(uiState.error, color = MaterialTheme.colorScheme.error)
        }
    }
}