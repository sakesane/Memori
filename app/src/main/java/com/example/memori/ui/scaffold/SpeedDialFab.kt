package com.example.memori.ui.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.memori.R

@Composable
fun SpeedDialFab(
    onImportClick: () -> Unit,
    onCreateDeckClick: () -> Unit,
    onInsertCardClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 45f else 0f, label = "")

    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 子按钮
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(bottom = 56.dp, end = 8.dp)
        ) {
            AnimatedVisibility(visible = expanded) {
                ExtendedFloatingActionButton(
                    onClick = {
                        expanded = false
                        onImportClick()
                    },
                    icon = { Icon(painterResource(id = R.drawable.download_24dp), contentDescription = "导入卡组") },
                    text = { Text("导入卡组") },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
            AnimatedVisibility(visible = expanded) {
                ExtendedFloatingActionButton(
                    onClick = {
                        expanded = false
                        onCreateDeckClick()
                    },
                    icon = { Icon(painterResource(id = R.drawable.create_new_folder_24dp), contentDescription = "创建卡组") },
                    text = { Text("创建卡组") },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
            AnimatedVisibility(visible = expanded) {
                ExtendedFloatingActionButton(
                    onClick = {
                        expanded = false
                        onInsertCardClick()
                    },
                    icon = { Icon(painterResource(id = R.drawable.note_stack_add_24dp), contentDescription = "插入卡片") },
                    text = { Text("插入卡片") },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // 主按钮
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.BottomEnd),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "更多操作",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}