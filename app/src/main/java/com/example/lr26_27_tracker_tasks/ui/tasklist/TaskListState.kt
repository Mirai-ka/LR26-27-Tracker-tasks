package com.example.lr26_27_tracker_tasks.ui.tasklist

import com.example.lr26_27_tracker_tasks.data.model.Task

sealed class TaskListState {
    object Loading : TaskListState()
    data class Success(val tasks: List<Task>) : TaskListState()
    data class Error(val message: String) : TaskListState()
}

sealed class TaskEditState {
    object Idle : TaskEditState()
    object Saving : TaskEditState()
    data class Success(val task: Task) : TaskEditState()
    data class Error(val message: String) : TaskEditState()
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}