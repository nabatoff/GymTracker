package com.example.gymtracker.ui.programs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gymtracker.data.entity.WorkoutProgram
import com.example.gymtracker.ui.viewmodel.ProgramsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramsScreen(
    navController: NavController,
    viewModel: ProgramsViewModel = viewModel()
) {
    val programs by viewModel.programs.collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredPrograms = remember(programs, searchQuery) {
        if (searchQuery.isBlank()) {
            programs
        } else {
            programs.filter { 
                it.name.contains(searchQuery, ignoreCase = true) 
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Программы тренировок") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить программу")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Поиск программ...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                singleLine = true
            )

            if (filteredPrograms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (searchQuery.isNotBlank()) 
                            "Программы не найдены" 
                        else 
                            "Нет программ. Добавьте первую программу!"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredPrograms,
                        key = { it.programId }
                    ) { program ->
                        ProgramCardWithDelete(
                            program = program,
                            onDelete = { viewModel.deleteProgram(program) },
                            onClick = {
                                navController.navigate("program_detail/${program.programId}")
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProgramDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.addProgram(name) {
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
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (programName.isNotBlank()) {
                        onConfirm(programName)
                    }
                }
            ) {
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
