package com.example.lr26_27_tracker_tasks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String = "",
    val user_id: String = "",
    val title: String = "",
    val description: String = "",
    val due_date: Long = 0L,
    val is_done: Boolean = false,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)