package com.example.gymtracker.ui.screens.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.data.local.entity.ProgramWithExercises
import com.example.gymtracker.data.local.entity.WorkoutProgram
import com.example.gymtracker.ui.viewmodel.ProgramsViewModel

@Composable
fun ProgramsScreen(
    viewModel: ProgramsViewModel,
    onProgramSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val programs by viewModel.programs.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var programToEdit by remember { mutableStateOf<WorkoutProgram?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.programs_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        if (programs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.empty_programs),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(programs) { programWithExercises ->
                    ProgramCard(
                        programWithExercises = programWithExercises,
                        onProgramClick = { onProgramSelected(programWithExercises.program.programId) },
                        onEdit = { programToEdit = programWithExercises.program },
                        onDelete = { viewModel.deleteProgram(programWithExercises.program) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ProgramDialog(
            title = stringResource(id = R.string.add_program),
            initialName = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.addProgram(name)
                showAddDialog = false
            }
        )
    }

    programToEdit?.let { program ->
        ProgramDialog(
            title = stringResource(id = R.string.update),
            initialName = program.name,
            onDismiss = { programToEdit = null },
            onConfirm = { newName ->
                viewModel.updateProgram(program, newName)
                programToEdit = null
            }
        )
    }
}

@Composable
private fun ProgramDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    val isValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onConfirm(name.trim())
                    }
                },
                enabled = isValid
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = stringResource(id = R.string.program_name_hint)) },
                singleLine = true,
                isError = !isValid,
                supportingText = {
                    if (!isValid) {
                        Text(text = stringResource(id = R.string.enter_name_error))
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramCard(
    programWithExercises: ProgramWithExercises,
    onProgramClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        onClick = onProgramClick,
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = programWithExercises.program.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (programWithExercises.exercises.isEmpty()) {
                    stringResource(id = R.string.no_exercises)
                } else {
                    stringResource(id = R.string.exercise_count, programWithExercises.exercises.size)
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}
