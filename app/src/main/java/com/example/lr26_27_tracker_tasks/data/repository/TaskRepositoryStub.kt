package com.example.lr26_27_tracker_tasks.data.repository

import com.example.lr26_27_tracker_tasks.data.api.FakeTaskApiService
import com.example.lr26_27_tracker_tasks.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepositoryStub {

    private val api = FakeTaskApiService()

    fun getAllTasks(): Flow<List<Task>> = flow {
        val tasks = api.getTasks()
        emit(tasks)
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