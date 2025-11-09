package com.example.gymtracker.ui.screens.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.data.local.entity.Exercise
import com.example.gymtracker.ui.viewmodel.ProgramsViewModel

@Composable
fun ProgramDetailScreen(
    programId: Long,
    viewModel: ProgramsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val programFlow = remember(programId) { viewModel.observeProgram(programId) }
    val programState by programFlow.collectAsState(initial = null)

    var showAddDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<Exercise?>(null) }

    val title by rememberUpdatedState(programState?.program?.name ?: stringResource(id = R.string.programs_title))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        val programWithExercises = programState
        if (programWithExercises == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.loading), style = MaterialTheme.typography.bodyLarge)
            }
        } else if (programWithExercises.exercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.no_exercises))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(programWithExercises.exercises) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onEdit = { exerciseToEdit = exercise },
                        onDelete = { viewModel.deleteExercise(exercise) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ExerciseDialog(
            title = stringResource(id = R.string.add_exercise),
            initialName = "",
            initialSets = "",
            initialReps = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, sets, reps ->
                viewModel.addExercise(programId, name, sets, reps)
                showAddDialog = false
            }
        )
    }

    exerciseToEdit?.let { exercise ->
        ExerciseDialog(
            title = stringResource(id = R.string.update),
            initialName = exercise.name,
            initialSets = exercise.sets.toString(),
            initialReps = exercise.reps,
            onDismiss = { exerciseToEdit = null },
            onConfirm = { name, sets, reps ->
                viewModel.updateExercise(exercise, name, sets, reps)
                exerciseToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(id = R.string.sets_label, exercise.sets),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = stringResource(id = R.string.reps_label, exercise.reps),
                style = MaterialTheme.typography.bodyMedium
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

@Composable
private fun ExerciseDialog(
    title: String,
    initialName: String,
    initialSets: String,
    initialReps: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var sets by remember(initialSets) { mutableStateOf(initialSets) }
    var reps by remember(initialReps) { mutableStateOf(initialReps) }

    val setsInt = sets.toIntOrNull()
    val isValid = name.isNotBlank() && setsInt != null && setsInt > 0 && reps.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onConfirm(name.trim(), setsInt!!, reps.trim())
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
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.exercise_name_hint)) },
                    singleLine = true,
                    isError = name.isBlank()
                )
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it.filter { char -> char.isDigit() } },
                    label = { Text(text = stringResource(id = R.string.sets_hint)) },
                    singleLine = true,
                    isError = setsInt == null || setsInt <= 0,
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text(text = stringResource(id = R.string.reps_hint)) },
                    singleLine = true,
                    isError = reps.isBlank(),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )
}
