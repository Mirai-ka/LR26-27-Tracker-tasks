package com.example.lr26_27_tracker_tasks.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lr26_27_tracker_tasks.data.auth.FirebaseAuthManager
import com.example.lr26_27_tracker_tasks.data.auth.TokenManager
import com.example.lr26_27_tracker_tasks.ui.tasklist.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: FirebaseAuthManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            val result = authManager.signInWithEmail(email, password)
            result.onSuccess { user ->
                val token = authManager.getIdToken() ?: ""
                tokenManager.saveAuthData(token, user.uid, email)
                _state.value = AuthUiState.Success(user.uid)
            }.onFailure { error ->
                _state.value = AuthUiState.Error(error.message ?: "Sign in failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            val result = authManager.signUpWithEmail(email, password)
            result.onSuccess { user ->
                val token = authManager.getIdToken() ?: ""
                tokenManager.saveAuthData(token, user.uid, email)
                _state.value = AuthUiState.Success(user.uid)
            }.onFailure { error ->
                _state.value = AuthUiState.Error(error.message ?: "Sign up failed")
            }
        }
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }
}