package com.example.lr26_27_tracker_tasks.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.lr26_27_tracker_tasks.data.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskStorage(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("task_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString("tasks", json).apply()
    }

    fun loadTasks(): List<Task> {
        val json = prefs.getString("tasks", null) ?: return emptyList()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTask(task: Task): List<Task> {
        val tasks = loadTasks().toMutableList()
        tasks.add(task)
        saveTasks(tasks)
        return tasks
    }

    fun deleteTask(taskId: String): List<Task> {
        val tasks = loadTasks().toMutableList()
        tasks.removeAll { it.id == taskId }
        saveTasks(tasks)
        return tasks
    }
}