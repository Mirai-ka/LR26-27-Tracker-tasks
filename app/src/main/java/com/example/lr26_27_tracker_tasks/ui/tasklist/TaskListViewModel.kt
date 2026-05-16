package com.example.lr26_27_tracker_tasks.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lr26_27_tracker_tasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TaskListState>(TaskListState.Loading)
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _state.value = TaskListState.Loading
            val result = repository.getAllTasks()
            result.onSuccess { tasks ->
                _state.value = TaskListState.Success(tasks)
            }.onFailure { error ->
                _state.value = TaskListState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val result = repository.deleteTask(taskId)
            result.onSuccess {
                loadTasks()
            }.onFailure { error ->
                _state.value = TaskListState.Error(error.message ?: "Delete failed")
            }
        }
    }
}