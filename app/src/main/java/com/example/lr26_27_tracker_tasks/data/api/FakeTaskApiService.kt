package com.example.lr26_27_tracker_tasks.data.api

import com.example.lr26_27_tracker_tasks.data.model.Task

class FakeTaskApiService {

    private val tasks = mutableListOf<Task>()

    suspend fun getTasks(): List<Task> {
        kotlinx.coroutines.delay(300)
        return tasks.toList()
    }

    suspend fun createTask(title: String, description: String, dueDate: Long = 0L): Task {
        kotlinx.coroutines.delay(500)
        val newTask = Task(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description,
            due_date = dueDate,
            is_done = false,
            created_at = System.currentTimeMillis()
        )
        tasks.add(newTask)
        return newTask
    }

    suspend fun updateTask(task: Task): Task {
        kotlinx.coroutines.delay(500)
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(updated_at = System.currentTimeMillis())
        }
        return task
    }

    suspend fun deleteTask(id: String): Boolean {
        kotlinx.coroutines.delay(300)
        return tasks.removeIf { it.id == id }
    }
}