package com.example.lr26_27_tracker_tasks.ui.taskedit

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lr26_27_tracker_tasks.data.auth.TokenManager
import com.example.lr26_27_tracker_tasks.data.repository.TaskRepository
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskEditState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: String?,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val repository = TaskRepository(tokenManager)

    val viewModel: TaskEditViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                TaskEditViewModel(repository)
            }
        }
    )

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is TaskEditState.Success) {
            viewModel.resetState()
            onSaveSuccess()
        }
    }

    val showDatePicker = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)
                dueDate = selected.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (taskId == null) "Create Task" else "Edit Task",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("📅 Due Date")
                Text(
                    text = dueDate?.let { formatDate(it) } ?: "Not set",
                    color = if (dueDate == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
            }
        }

        if (dueDate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { dueDate = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Clear due date")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is TaskEditState.Saving -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            is TaskEditState.Error -> {
                Text(
                    text = (state as TaskEditState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.saveTask(title, description, dueDate ?: 0L) },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && state !is TaskEditState.Saving
        ) {
            Text("Save")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(timestamp))
}