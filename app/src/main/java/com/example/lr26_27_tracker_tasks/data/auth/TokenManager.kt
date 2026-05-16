package com.example.lr26_27_tracker_tasks.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_FIREBASE_TOKEN = "firebase_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkTokenValidity()
    }

    private fun checkTokenValidity() {
        val token = getFirebaseToken()
        val userId = getUserId()

        if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            _authState.value = AuthState.Authenticated(userId)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun saveAuthData(firebaseToken: String, userId: String, email: String) {
        sharedPreferences.edit()
            .putString(KEY_FIREBASE_TOKEN, firebaseToken)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .apply()
        _authState.value = AuthState.Authenticated(userId)
    }

    fun getFirebaseToken(): String? = sharedPreferences.getString(KEY_FIREBASE_TOKEN, null)

    fun getUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)

    fun getUserEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)

    fun clearAuthData() {
        sharedPreferences.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val userId: String) : AuthState()
}