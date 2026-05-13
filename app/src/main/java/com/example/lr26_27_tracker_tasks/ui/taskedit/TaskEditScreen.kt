package com.example.lr26_27_tracker_tasks.ui.taskedit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lr26_27_tracker_tasks.ui.tasklist.TaskEditState

@Composable
fun TaskEditScreen(
    taskId: String?,
    onSaveSuccess: () -> Unit,
    viewModel: TaskEditViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(state) {
        if (state is TaskEditState.Success) {
            onSaveSuccess()
            viewModel.resetState()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
            label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
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
            onClick = { viewModel.saveTask(title, description) },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && state !is TaskEditState.Saving
        ) {
            Text("Save Task")
        }
    }
}
