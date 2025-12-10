package com.example.project

data class TodoItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String?,
    val imageUri: String?,    // Uri.toString()
    val remindAt: Long?,      // timestamp в миллисекундах
    val isDone: Boolean = false
)
