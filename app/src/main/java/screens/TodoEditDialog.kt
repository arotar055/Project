package com.example.project

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoEditDialog(
    initialItem: TodoItem?,
    onClose: () -> Unit,
    onSave: (TodoItem) -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var imageUri by remember { mutableStateOf(initialItem?.imageUri?.let { Uri.parse(it) }) }
    var remindAt by remember { mutableStateOf(initialItem?.remindAt) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) imageUri = it
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(if (initialItem == null) "Добавить задачу" else "Редактировать") },
        text = {
            Column {
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
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                Button(onClick = { picker.launch("image/*") }) {
                    Text("Фото")
                }

                imageUri?.let {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.height(150.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    val newItem = TodoItem(
                        id = initialItem?.id ?: System.currentTimeMillis(),
                        title = title,
                        description = description.takeIf { it.isNotBlank() },
                        imageUri = imageUri?.toString(),
                        remindAt = remindAt,
                        isDone = initialItem?.isDone ?: false
                    )
                    onSave(newItem)
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Отмена") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
