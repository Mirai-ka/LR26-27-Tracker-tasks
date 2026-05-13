package com.example.lr26_27_tracker_tasks.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lr26_27_tracker_tasks.ui.tasklist.AuthUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            delay(1000)
            
            if (email.isNotBlank() && password.isNotBlank()) {
                _state.value = AuthUiState.Success("user_${System.currentTimeMillis()}")
            } else {
                _state.value = AuthUiState.Error("Invalid email or password")
            }
        }
    }
    
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            delay(1000)
            
            if (email.isNotBlank() && password.isNotBlank() && password.length >= 6) {
                _state.value = AuthUiState.Success("user_${System.currentTimeMillis()}")
            } else {
                _state.value = AuthUiState.Error("Email required and password must be at least 6 characters")
            }
        }
    }
    
    fun resetState() {
        _state.value = AuthUiState.Idle
    }
}
