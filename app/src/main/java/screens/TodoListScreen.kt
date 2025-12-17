package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project.*
import com.example.project.snow.Snowfall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    var items by remember { mutableStateOf(TodoRepository.getItems()) }
    var reloadKey by remember { mutableStateOf(0) }
    var editingItem by remember { mutableStateOf<TodoItem?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

    var sortOption by remember { mutableStateOf(SortOption.TIME_ASC) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    fun reload() {
        items = TodoRepository.getItems()
            .sortedWith(compareBy<TodoItem> { it.isDone }.then(todoComparator(sortOption)))
    }

    LaunchedEffect(reloadKey, sortOption) { reload() }

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
                    onCheckedChange = { onThemeChange(it) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingItem = null
                isDialogOpen = true
            }) { Text("+") }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ❄️ СНЕЖИНКИ ВОЗВРАЩЕНЫ
            Snowfall(
                flakesCount = 100,
                modifier = Modifier.fillMaxSize()
            )

            LazyColumn(Modifier.fillMaxSize()) {
                items(items, key = { it.id }) { item ->
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
                            ReminderScheduler.cancelReminder(context, item.id)
                            TodoRepository.delete(item.id)
                            reloadKey++
                        }
                    )
                }
            }

            // сортировка + удалить всё (слева снизу)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                FloatingActionButton(onClick = { sortMenuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Сортировка")
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.title) },
                            onClick = {
                                sortOption = opt
                                sortMenuExpanded = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Удалить всё") },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                        onClick = {
                            TodoRepository.getItems().forEach {
                                ReminderScheduler.cancelReminder(context, it.id)
                            }
                            TodoRepository.deleteAll()
                            reloadKey++
                            sortMenuExpanded = false
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
            onSave = { saved ->
                TodoRepository.addOrUpdate(saved)
                if (saved.remindAt != null) {
                    ReminderScheduler.scheduleReminder(context, saved)
                } else {
                    ReminderScheduler.cancelReminder(context, saved.id)
                }
                reloadKey++
                isDialogOpen = false
            }
        )
    }
}
