package com.example.gymtracker.ui.programs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProgramsScreen(
    onProgramClick: (Long) -> Unit,
    viewModel: ProgramsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить программу")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.programs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет программ. Добавьте первую!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.programs) { program ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onProgramClick(program.programId) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = program.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(onClick = { viewModel.deleteProgram(program) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProgramDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                if (name.isNotBlank()) {
                    viewModel.addProgram(name)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun AddProgramDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var programName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая программа") },
        text = {
            OutlinedTextField(
                value = programName,
                onValueChange = { programName = it },
                label = { Text("Название программы") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(programName) }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
