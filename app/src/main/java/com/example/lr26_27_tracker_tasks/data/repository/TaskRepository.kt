package com.example.lr26_27_tracker_tasks.data.repository

import com.example.lr26_27_tracker_tasks.data.auth.TokenManager
import com.example.lr26_27_tracker_tasks.data.database.SupabaseClientProvider
import com.example.lr26_27_tracker_tasks.data.model.Profile
import com.example.lr26_27_tracker_tasks.data.model.Task
import com.example.lr26_27_tracker_tasks.utils.Constants
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface TaskApiService {
    @GET("tasks")
    suspend fun getTasks(): retrofit2.Response<List<Task>>

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") id: String): retrofit2.Response<Task>

    @POST("tasks")
    suspend fun createTask(@Body task: Task): retrofit2.Response<Task>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: Task): retrofit2.Response<Task>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): retrofit2.Response<Unit>
}

class TaskRepository(
    private val tokenManager: TokenManager
) {

    private val supabaseClient = SupabaseClientProvider.client

    private val retrofitApi: TaskApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenManager.getFirebaseToken()
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApiService::class.java)
    }

    private suspend fun getLocalUserId(): String? {
        val firebaseUid = tokenManager.getUserId() ?: return null

        val profile = try {
            supabaseClient.postgrest[Constants.TABLE_PROFILES]
                .select {
                    filter { eq("firebase_uid", firebaseUid) }
                }
                .decodeSingleOrNull<Profile>()
        } catch (e: Exception) {
            null
        }

        return profile?.id ?: createUserProfile(firebaseUid)
    }

    private suspend fun createUserProfile(firebaseUid: String): String {
        val email = tokenManager.getUserEmail() ?: ""
        val newProfile = Profile(
            firebase_uid = firebaseUid,
            email = email
        )

        val created = supabaseClient.postgrest[Constants.TABLE_PROFILES]
            .insert(newProfile) { select() }
            .decodeSingle<Profile>()

        return created.id
    }

    suspend fun getAllTasks(): Result<List<Task>> = withContext(Dispatchers.IO) {
        try {
            val tasks = supabaseClient.postgrest[Constants.TABLE_TASKS]
                .select {
                    order("created_at") { ascending = false }
                }
                .decodeList<Task>()
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(title: String, description: String, dueDate: Long): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val userId = getLocalUserId() ?: return@withContext Result.failure(Exception("User not found"))
            val newTask = Task(
                user_id = userId,
                title = title,
                description = description,
                due_date = dueDate
            )
            val response = retrofitApi.createTask(newTask)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Fallback на Supabase
                val created = supabaseClient.postgrest[Constants.TABLE_TASKS]
                    .insert(newTask) { select() }
                    .decodeSingle<Task>()
                Result.success(created)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val response = retrofitApi.updateTask(task.id, task)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val updated = supabaseClient.postgrest[Constants.TABLE_TASKS]
                    .update(task) {
                        filter { eq("id", task.id) }
                        select()
                    }
                    .decodeSingle<Task>()
                Result.success(updated)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = retrofitApi.deleteTask(taskId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                supabaseClient.postgrest[Constants.TABLE_TASKS]
                    .delete { filter { eq("id", taskId) } }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}