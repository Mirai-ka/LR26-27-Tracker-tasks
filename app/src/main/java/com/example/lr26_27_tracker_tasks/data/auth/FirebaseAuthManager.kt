package com.example.lr26_27_tracker_tasks.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<FirebaseAuthState>(FirebaseAuthState.Unauthenticated)
    val authState: StateFlow<FirebaseAuthState> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = if (firebaseAuth.currentUser != null) {
                FirebaseAuthState.Authenticated(firebaseAuth.currentUser!!)
            } else {
                FirebaseAuthState.Unauthenticated
            }
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getIdToken(): String? {
        return getCurrentUser()?.getIdToken(false)?.await()?.token
    }
}

sealed class FirebaseAuthState {
    object Unauthenticated : FirebaseAuthState()
    data class Authenticated(val user: FirebaseUser) : FirebaseAuthState()
}