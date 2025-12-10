package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project.snow.Snowfall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    var items by remember { mutableStateOf(TodoRepository.getItems()) }
    var reloadKey by remember { mutableStateOf(0) }
    var editingItem by remember { mutableStateOf<TodoItem?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

    fun reload() {
        items = TodoRepository.getItems()
    }

    LaunchedEffect(reloadKey) {
        reload()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🎄 Мой праздничный список дел",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Switch(
                    checked = darkTheme,
                    onCheckedChange = { onThemeChange() }
                )
            }
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingItem = null
                isDialogOpen = true
            }) {
                Text("+")
            }
        }
    ) { pad ->

        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(Color(0xFF002233))
        ) {

            // ❄ СНЕГ ПОЛНОСТЬЮ РАБОЧИЙ
            Snowfall(flakesCount = 100, modifier = Modifier.fillMaxSize())

            LazyColumn(Modifier.fillMaxSize()) {
                items(items) { item ->

                    TodoRow(
                        item = item,
                        onCheckedChange = { isDone ->
                            TodoRepository.addOrUpdate(item.copy(isDone = isDone))
                            reloadKey++
                        },
                        onClick = {
                            editingItem = item
                            isDialogOpen = true
                        },
                        onDelete = {
                            TodoRepository.delete(item.id)
                            reloadKey++
                        }
                    )
                }
            }
        }
    }

    if (isDialogOpen) {
        TodoEditDialog(
            initialItem = editingItem,
            onClose = { isDialogOpen = false },
            onSave = {
                TodoRepository.addOrUpdate(it)
                reloadKey++
                isDialogOpen = false
            }
        )
    }
}
