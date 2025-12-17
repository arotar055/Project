package screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.project.TodoItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditDialog(
    initialItem: TodoItem?,
    onClose: () -> Unit,
    onSave: (TodoItem) -> Unit
) {
    val context = LocalContext.current

    var title by remember(initialItem?.id) { mutableStateOf(initialItem?.title ?: "") }
    var description by remember(initialItem?.id) { mutableStateOf(initialItem?.description ?: "") }
    var imageUri by remember(initialItem?.id) { mutableStateOf(initialItem?.imageUri) }
    var remindAt by remember(initialItem?.id) { mutableStateOf<Long?>(initialItem?.remindAt) }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri?.toString()
    }

    fun pickDateTime() {
        val cal = Calendar.getInstance().apply {
            if (remindAt != null) timeInMillis = remindAt!!
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        remindAt = cal.timeInMillis
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Редактировать") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { picker.launch("image/*") }) {
                        Text(if (imageUri.isNullOrBlank()) "Фото" else "Изменить фото")
                    }

                    Button(onClick = { pickDateTime() }) {
                        Text(
                            remindAt?.let { "Напомнить: ${dateFormat.format(Date(it))}" }
                                ?: "Выбрать время"
                        )
                    }
                }

                if (remindAt != null) {
                    TextButton(onClick = { remindAt = null }) {
                        Text("Убрать напоминание")
                    }
                }

                if (!imageUri.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank()) return@Button
                onSave(
                    TodoItem(
                        id = initialItem?.id ?: System.currentTimeMillis(),
                        title = title,
                        description = description.ifBlank { null },
                        imageUri = imageUri,
                        remindAt = remindAt,
                        isDone = initialItem?.isDone ?: false
                    )
                )
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Отмена") } }
    )
}
