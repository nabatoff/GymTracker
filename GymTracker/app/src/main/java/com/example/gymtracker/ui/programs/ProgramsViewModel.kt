package com.example.gymtracker.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entities.Exercise
import com.example.gymtracker.data.local.entities.ProgramWithExercises
import com.example.gymtracker.data.local.entities.WorkoutProgram
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgramsUiState(
    val programs: List<ProgramWithExercises> = emptyList(),
    val isBusy: Boolean = false,
    val errorMessage: String? = null
)

class ProgramsViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val programsFlow: StateFlow<List<ProgramWithExercises>> =
        repository.programs
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val uiState: StateFlow<ProgramsUiState> =
        programsFlow
            .map { ProgramsUiState(programs = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProgramsUiState()
            )

    fun observeProgram(programId: Long): Flow<ProgramWithExercises?> =
        repository.observeProgram(programId)

    fun addProgram(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addProgram(WorkoutProgram(name = name.trim()))
        }
    }

    fun updateProgram(programId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.updateProgram(
                WorkoutProgram(
                    programId = programId,
                    name = name.trim()
                )
            )
        }
    }

    fun deleteProgram(program: WorkoutProgram) {
        viewModelScope.launch {
            repository.deleteProgram(program)
        }
    }

    fun addExercise(
        programId: Long,
        name: String,
        sets: Int,
        reps: String
    ) {
        if (name.isBlank() || sets <= 0 || reps.isBlank()) return
        viewModelScope.launch {
            repository.addExercise(
                Exercise(
                    programId = programId,
                    name = name.trim(),
                    sets = sets,
                    reps = reps.trim()
                )
            )
        }
    }

    fun updateExercise(
        exerciseId: Long,
        programId: Long,
        name: String,
        sets: Int,
        reps: String
    ) {
        if (name.isBlank() || sets <= 0 || reps.isBlank()) return
        viewModelScope.launch {
            repository.updateExercise(
                Exercise(
                    exerciseId = exerciseId,
                    programId = programId,
                    name = name.trim(),
                    sets = sets,
                    reps = reps.trim()
                )
            )
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }
}
