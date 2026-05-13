package com.example.lr26_27_tracker_tasks.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lr26_27_tracker_tasks.data.model.Task
import com.example.lr26_27_tracker_tasks.data.repository.TaskRepositoryStub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    
    private val repository = TaskRepositoryStub()
    
    private val _state = MutableStateFlow<TaskListState>(TaskListState.Loading)
    val state: StateFlow<TaskListState> = _state.asStateFlow()
    
    init {
        loadTasks()
    }
    
    fun loadTasks() {
        viewModelScope.launch {
            _state.value = TaskListState.Loading
            try {
                repository.getAllTasks().collect { tasks ->
                    _state.value = TaskListState.Success(tasks)
                }
            } catch (e: Exception) {
                _state.value = TaskListState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            loadTasks()
        }
    }
    
    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.createTask(task.title, task.description)
            loadTasks()
        }
    }
}
