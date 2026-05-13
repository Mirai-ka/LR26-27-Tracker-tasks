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
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskEditState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: String?,
    onSaveSuccess: () -> Unit,
    viewModel: TaskEditViewModel = viewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    
    val state by viewModel.state.collectAsState()
    
    // Реагируем на успешное сохранение
    LaunchedEffect(state) {
        if (state is TaskEditState.Success) {
            println("📝 [TaskEditScreen] Save success, calling onSaveSuccess")
            viewModel.resetState()
            onSaveSuccess()
        }
        if (state is TaskEditState.Error) {
            println("❌ [TaskEditScreen] Save error: ${(state as TaskEditState.Error).message}")
        }
    }
    
    val showDatePicker = {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                dueDate = selectedDate.timeInMillis
                println("📅 [TaskEditScreen] Date selected: ${formatDate(dueDate!!)}")
            },
            year, month, day
        ).show()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (taskId == null) "Create New Task" else "Edit Task",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title *") },
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 Due Date",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dueDate?.let { formatDate(it) } ?: "Not set",
                    color = if (dueDate == null) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        if (dueDate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { dueDate = null },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear due date")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (state) {
            is TaskEditState.Saving -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }
            is TaskEditState.Error -> {
                Text(
                    text = (state as TaskEditState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            else -> {}
        }
        
        Button(
            onClick = { 
                if (title.isNotBlank()) {
                    println("📝 [TaskEditScreen] Save button clicked: title='$title'")
                    viewModel.saveTask(
                        title = title,
                        description = description,
                        dueDate = dueDate ?: 0L
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && state !is TaskEditState.Saving
        ) {
            Text(if (taskId == null) "Create Task" else "Update Task")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return format.format(date)
}
