package com.example.lr26_27_tracker_tasks.data.repository

import com.example.lr26_27_tracker_tasks.data.api.FakeTaskApiService
import com.example.lr26_27_tracker_tasks.data.model.Task

class TaskRepositoryStub {

    private val api = FakeTaskApiService()

    // Возвращаем список синхронно (без Flow для простоты)
    suspend fun getAllTasks(): List<Task> {
        return api.getTasks()
    }

    suspend fun createTask(title: String, description: String, dueDate: Long = 0L): Result<Task> {
        return try {
            val task = api.createTask(title, description, dueDate)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val updated = api.updateTask(task)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            api.deleteTask(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}