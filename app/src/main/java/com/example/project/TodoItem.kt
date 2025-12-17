package com.example.project

data class TodoItem(
    val id: Long,
    val title: String,
    val description: String? = null,
    val imageUri: String? = null,
    val remindAt: Long? = null,
    val isDone: Boolean = false
)
