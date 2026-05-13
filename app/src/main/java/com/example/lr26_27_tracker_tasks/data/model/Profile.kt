package com.example.lr26_27_tracker_tasks.data.model

data class Profile(
    val id: String = "",
    val firebase_uid: String = "",
    val email: String = "",
    val created_at: Long = System.currentTimeMillis()
)
