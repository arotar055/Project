package com.example.project

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.project.ui.theme.ProjectTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // инициализация хранилища задач
        TodoRepository.init(applicationContext)

        enableEdgeToEdge()
        setContent {
            ProjectTheme {
                ToDoApp()
            }
        }
    }
}

// ---------- Навигация по экранам ----------

sealed class Screen {
    object List : Screen()
    object Edit : Screen()
}

@Composable
fun ToDoApp() {
    val context = LocalContext.current

    // Запрос разрешения на уведомления (Android 13+)
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { /* granted / denied – игнорируем */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
    var editingItem by remember { mutableStateOf<TodoItem?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    when (currentScreen) {
        is Screen.List -> TodoListScreen(
            reloadKey = reloadKey,
            onAddClick = {
                editingItem = null
                currentScreen = Screen.Edit
            },
            onEditClick = { item ->
                editingItem = item
                currentScreen = Screen.Edit
            }
        )

        is Screen.Edit -> TodoEditScreen(
            initialItem = editingItem,
            onDone = {
                reloadKey++
                currentScreen = Screen.List
            }
        )
    }
}

// ---------- Экран списка задач ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    reloadKey: Int,
    onAddClick: () -> Unit,
    onEditClick: (TodoItem) -> Unit
) {
    var items by remember { mutableStateOf(emptyList<TodoItem>()) }

    fun reload() {
        items = TodoRepository
            .getItems()
            .sortedBy { it.isDone } // невыполненные сверху, выполненные снизу
    }

    LaunchedEffect(reloadKey) {
        reload()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Пока нет задач, нажми +")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    TodoRow(
                        item = item,
                        onCheckedChange = { checked ->
                            val updated = item.copy(isDone = checked)
                            TodoRepository.addOrUpdate(updated)
                            reload()
                        },
                        onClick = { onEditClick(item) },
                        onDelete = {
                            TodoRepository.delete(item.id)
                            reload()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoRow(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = onCheckedChange
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(item.title, fontWeight = FontWeight.Bold)

            if (!item.description.isNullOrBlank()) {
                Text(
                    item.description!!,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (item.remindAt != null) {
                Text(
                    "Напоминание: ${dateFormat.format(Date(item.remindAt))}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (item.imageUri != null) {
                Spacer(Modifier.height(4.dp))
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = "Фото задачи",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Button(onClick = onDelete) {
            Text("Удалить")
        }
    }
}

// ---------- Экран создания/редактирования ----------

@Composable
fun TodoEditScreen(
    initialItem: TodoItem?,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    // Состояние привязано к id, чтобы при открытии другой задачи данные обновлялись
    var title by remember(initialItem?.id) {
        mutableStateOf(initialItem?.title ?: "")
    }
    var description by remember(initialItem?.id) {
        mutableStateOf(initialItem?.description ?: "")
    }
    var imageUri by remember(initialItem?.id) {
        mutableStateOf<Uri?>(initialItem?.imageUri?.let { Uri.parse(it) })
    }
    var remindAt by remember(initialItem?.id) {
        mutableStateOf<Long?>(initialItem?.remindAt)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val dateFormat = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text(if (imageUri == null) "Добавить фото" else "Изменить фото")
        }

        if (imageUri != null) {
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = imageUri,
                contentDescription = "Выбранное фото",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance().apply {
                if (remindAt != null) {
                    timeInMillis = remindAt!!
                }
            }

            // выбор даты
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // затем выбор времени
                    android.app.TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)

                            remindAt = calendar.timeInMillis
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text(
                text = remindAt?.let {
                    "Напоминание: ${dateFormat.format(Date(it))}"
                } ?: "Выбрать дату и время напоминания"
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isBlank()) return@Button

                val item = TodoItem(
                    id = initialItem?.id ?: System.currentTimeMillis(),
                    title = title,
                    description = description.ifBlank { null },
                    imageUri = imageUri?.toString(),
                    remindAt = remindAt,
                    isDone = initialItem?.isDone ?: false
                )

                TodoRepository.addOrUpdate(item)

                if (item.remindAt != null) {
                    ReminderScheduler.scheduleReminder(context, item)
                } else {
                    ReminderScheduler.cancelReminder(context, item.id)
                }

                onDone()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}
