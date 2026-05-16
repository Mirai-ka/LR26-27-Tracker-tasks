package com.example.lr26_27_tracker_tasks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lr26_27_tracker_tasks.data.auth.TokenManager
import com.example.lr26_27_tracker_tasks.ui.auth.AuthScreen
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskListScreen
import com.example.lr26_27_tracker_tasks.ui.taskedit.TaskEditScreen
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    var isAuthenticated by remember { mutableStateOf(false) }

    // Проверяем статус авторизации при запуске
    LaunchedEffect(Unit) {
        tokenManager.authState.collect { state ->
            isAuthenticated = when (state) {
                is com.example.lr26_27_tracker_tasks.data.auth.AuthState.Authenticated -> true
                else -> false
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "task_list" else "auth"
    ) {
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("task_list") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("task_list") {
            TaskListScreen(
                onNavigateToEdit = { taskId ->
                    navController.navigate("task_edit/${taskId ?: "new"}")
                },
                onSignOut = {
                    tokenManager.clearAuthData()
                    navController.navigate("auth") {
                        popUpTo("task_list") { inclusive = true }
                    }
                }
            )
        }

        composable(
            "task_edit/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskEditScreen(
                taskId = if (taskId == "new") null else taskId,
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}