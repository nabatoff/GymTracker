package com.example.gymtracker.ui.programs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.data.local.entities.ProgramWithExercises
import com.example.gymtracker.data.local.entities.WorkoutProgram
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProgramsScreen(
    stateFlow: StateFlow<ProgramsUiState>,
    onCreateProgram: (String) -> Unit,
    onUpdateProgram: (Long, String) -> Unit,
    onDeleteProgram: (WorkoutProgram) -> Unit,
    onProgramClick: (Long) -> Unit
) {
    val state by stateFlow.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("Новая программа") }
    val programNameState = remember { mutableStateOf("") }
    val editingProgramId = remember { mutableStateOf<Long?>(null) }

    fun openDialog(title: String, initialName: String, programId: Long?) {
        dialogTitle.value = title
        programNameState.value = initialName
        editingProgramId.value = programId
        showDialog.value = true
    }

    fun closeDialog() {
        showDialog.value = false
        programNameState.value = ""
        editingProgramId.value = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.programs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Нет программ. Нажми + чтобы создать первую.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.programs, key = { it.program.programId }) { programWithExercises ->
                    ProgramCard(
                        programWithExercises = programWithExercises,
                        onProgramClick = { onProgramClick(programWithExercises.program.programId) },
                        onEdit = {
                            openDialog(
                                title = "Редактирование программы",
                                initialName = programWithExercises.program.name,
                                programId = programWithExercises.program.programId
                            )
                        },
                        onDelete = { onDeleteProgram(programWithExercises.program) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { openDialog("Новая программа", "", null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить программу")
        }
    }

    if (showDialog.value) {
        ProgramNameDialog(
            title = dialogTitle.value,
            nameState = programNameState,
            onDismiss = { closeDialog() },
            onConfirm = {
                val trimmed = programNameState.value.trim()
                if (trimmed.isNotEmpty()) {
                    val id = editingProgramId.value
                    if (id == null) {
                        onCreateProgram(trimmed)
                    } else {
                        onUpdateProgram(id, trimmed)
                    }
                }
                closeDialog()
            }
        )
    }
}

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
            .clickable { onProgramClick() },
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = programWithExercises.program.name,
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (programWithExercises.exercises.isEmpty()) {
                    "Упражнений пока нет"
                } else {
                    programWithExercises.exercises.joinToString { "${it.name} (${it.sets}x${it.reps})" }
                },
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProgramNameDialog(
    title: String,
    nameState: MutableState<String>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Название программы") }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
