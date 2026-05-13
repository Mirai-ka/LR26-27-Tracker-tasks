package com.example.lr26_27_tracker_tasks.ui.taskedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lr26_27_tracker_tasks.data.repository.TaskRepositoryStub
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskEditState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskEditViewModel : ViewModel() {
    
    private val repository = TaskRepositoryStub()
    
    private val _state = MutableStateFlow<TaskEditState>(TaskEditState.Idle)
    val state: StateFlow<TaskEditState> = _state.asStateFlow()
    
    fun saveTask(title: String, description: String) {
        viewModelScope.launch {
            _state.value = TaskEditState.Saving
            delay(500)
            val result = repository.createTask(title, description)
            result.onSuccess { task ->
                _state.value = TaskEditState.Success(task)
            }.onFailure { error ->
                _state.value = TaskEditState.Error(error.message ?: "Save failed")
            }
        }
    }
    
    fun resetState() {
        _state.value = TaskEditState.Idle
    }
}
