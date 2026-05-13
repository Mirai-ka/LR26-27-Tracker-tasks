package com.example.lr26_27_tracker_tasks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lr26_27_tracker_tasks.ui.auth.AuthScreen
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskListScreen
import com.example.lr26_27_tracker_tasks.ui.taskedit.TaskEditScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "task_list" else "auth"
    ) {
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    isAuthenticated = true
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
                    isAuthenticated = false
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
            val actualId = if (taskId == "new") null else taskId
            TaskEditScreen(
                taskId = actualId,
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
