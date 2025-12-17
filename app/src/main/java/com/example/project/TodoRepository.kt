package com.example.project

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TodoRepository {

    private const val PREF_NAME = "todo_prefs"
    private const val KEY_ITEMS = "todo_items"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<TodoItem>>() {}.type

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getItems(): List<TodoItem> {
        val json = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        return gson.fromJson(json, typeToken)
    }

    private fun saveItems(items: List<TodoItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_ITEMS, json).apply()
    }

    fun addOrUpdate(item: TodoItem) {
        val current = getItems().toMutableList()
        val index = current.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            current[index] = item
        } else {
            current.add(item)
        }
        saveItems(current)
    }

    fun delete(id: Long) {
        val current = getItems().filter { it.id != id }
        saveItems(current)
    }
    fun deleteAll() {
        saveItems(emptyList())
    }
}
