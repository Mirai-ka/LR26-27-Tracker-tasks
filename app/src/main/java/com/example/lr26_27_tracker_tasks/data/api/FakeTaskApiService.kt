package com.example.lr26_27_tracker_tasks.data.api

import com.example.lr26_27_tracker_tasks.data.model.Task

class FakeTaskApiService {

    // Сделаем список изменяемым и доступным
    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task>
        get() = _tasks.toList()

    suspend fun getTasks(): List<Task> {
        kotlinx.coroutines.delay(300)
        println("📋 Getting tasks, count: ${_tasks.size}")
        return _tasks.toList()
    }

    suspend fun createTask(title: String, description: String, dueDate: Long = 0L): Task {
        kotlinx.coroutines.delay(500)
        val newTask = Task(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description,
            due_date = dueDate,
            is_done = false,
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        _tasks.add(newTask)
        println("✅ Task created: ${newTask.title}, total tasks: ${_tasks.size}")
        return newTask
    }

    suspend fun updateTask(task: Task): Task {
        kotlinx.coroutines.delay(500)
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            _tasks[index] = task.copy(updated_at = System.currentTimeMillis())
            println("✏️ Task updated: ${task.title}")
        }
        return task
    }

    suspend fun deleteTask(id: String): Boolean {
        kotlinx.coroutines.delay(300)
        val removed = _tasks.removeIf { it.id == id }
        println("🗑️ Task deleted, remaining tasks: ${_tasks.size}")
        return removed
    }
}