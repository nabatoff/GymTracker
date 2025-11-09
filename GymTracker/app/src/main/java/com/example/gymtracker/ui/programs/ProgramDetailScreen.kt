package com.example.gymtracker.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.data.local.entities.Exercise
import com.example.gymtracker.data.local.entities.ProgramWithExercises
import com.example.gymtracker.data.local.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    programId: Long,
    programFlow: Flow<ProgramWithExercises?>,
    onBack: () -> Unit,
    onAddExercise: (Long, String, Int, String) -> Unit,
    onUpdateExercise: (Long, Long, String, Int, String) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    onRenameProgram: (Long, String) -> Unit,
    onDeleteProgram: (WorkoutProgram) -> Unit
) {
    val program by programFlow.collectAsStateWithLifecycle(initialValue = null)

    val showExerciseDialog = remember { mutableStateOf(false) }
    val exerciseDialogTitle = remember { mutableStateOf("Добавить упражнение") }
    val exerciseName = remember { mutableStateOf("") }
    val exerciseSets = remember { mutableIntStateOf(3) }
    val exerciseReps = remember { mutableStateOf("10") }
    val editingExercise = remember { mutableStateOf<Exercise?>(null) }

    val showRenameDialog = remember { mutableStateOf(false) }
    val newProgramName = remember { mutableStateOf(program?.program?.name ?: "") }

    LaunchedEffect(program?.program?.name) {
        newProgramName.value = program?.program?.name ?: ""
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = program?.program?.name ?: "Программа") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        program?.program?.let {
                            showRenameDialog.value = true
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Переименовать")
                    }
                    IconButton(onClick = {
                        program?.program?.let {
                            onDeleteProgram(it)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить программу")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    exerciseDialogTitle.value = "Добавить упражнение"
                    exerciseName.value = ""
                    exerciseSets.intValue = 3
                    exerciseReps.value = "10"
                    editingExercise.value = null
                    showExerciseDialog.value = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить упражнение")
            }
        }
    ) { innerPadding ->
        if (program == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Программа не найдена")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(program!!.exercises, key = { it.exerciseId }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onEdit = {
                            exerciseDialogTitle.value = "Редактирование упражнения"
                            exerciseName.value = exercise.name
                            exerciseSets.intValue = exercise.sets
                            exerciseReps.value = exercise.reps
                            editingExercise.value = exercise
                            showExerciseDialog.value = true
                        },
                        onDelete = { onDeleteExercise(exercise) }
                    )
                }
            }
        }
    }

    if (showExerciseDialog.value) {
        ExerciseDialog(
            title = exerciseDialogTitle.value,
            nameState = exerciseName,
            setsState = exerciseSets,
            repsState = exerciseReps,
            onDismiss = { showExerciseDialog.value = false },
            onConfirm = {
                val name = exerciseName.value.trim()
                val reps = exerciseReps.value.trim()
                val sets = exerciseSets.intValue
                if (name.isEmpty() || reps.isEmpty() || sets <= 0) return@ExerciseDialog
                val editing = editingExercise.value
                if (editing == null) {
                    onAddExercise(programId, name, sets, reps)
                } else {
                    onUpdateExercise(editing.exerciseId, programId, name, sets, reps)
                }
                showExerciseDialog.value = false
            }
        )
    }

    if (showRenameDialog.value && program != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog.value = false },
            title = { Text("Переименовать программу") },
            text = {
                OutlinedTextField(
                    value = newProgramName.value,
                    onValueChange = { newProgramName.value = it },
                    label = { Text("Название") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = newProgramName.value.trim()
                    if (trimmed.isNotEmpty()) {
                        onRenameProgram(program!!.program.programId, trimmed)
                        showRenameDialog.value = false
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog.value = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${exercise.sets} подходов × ${exercise.reps} повторений",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ExerciseDialog(
    title: String,
    nameState: androidx.compose.runtime.MutableState<String>,
    setsState: androidx.compose.runtime.MutableIntState,
    repsState: androidx.compose.runtime.MutableState<String>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    label = { Text("Название упражнения") }
                )
                OutlinedTextField(
                    value = setsState.intValue.toString(),
                    onValueChange = {
                        val parsed = it.toIntOrNull() ?: 0
                        setsState.intValue = parsed
                    },
                    label = { Text("Подходы") }
                )
                OutlinedTextField(
                    value = repsState.value,
                    onValueChange = { repsState.value = it },
                    label = { Text("Повторения") },
                    placeholder = { Text("8-12") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
