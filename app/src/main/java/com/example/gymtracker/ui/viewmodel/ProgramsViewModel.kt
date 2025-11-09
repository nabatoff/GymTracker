package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entity.Exercise
import com.example.gymtracker.data.local.entity.ProgramWithExercises
import com.example.gymtracker.data.local.entity.WorkoutProgram
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgramsViewModel(private val repository: AppRepository) : ViewModel() {

    val programs: StateFlow<List<ProgramWithExercises>> = repository
        .getAllProgramsWithExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun observeProgram(programId: Long) = repository.getProgramWithExercises(programId)

    fun addProgram(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertProgram(WorkoutProgram(name = name.trim()))
        }
    }

    fun updateProgram(program: WorkoutProgram, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            repository.updateProgram(program.copy(name = newName.trim()))
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
        if (name.isBlank() || reps.isBlank() || sets <= 0) return
        viewModelScope.launch {
            repository.insertExercise(
                Exercise(
                    programId = programId,
                    name = name.trim(),
                    sets = sets,
                    reps = reps.trim()
                )
            )
        }
    }

    fun updateExercise(exercise: Exercise, name: String, sets: Int, reps: String) {
        if (name.isBlank() || reps.isBlank() || sets <= 0) return
        viewModelScope.launch {
            repository.updateExercise(
                exercise.copy(
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
