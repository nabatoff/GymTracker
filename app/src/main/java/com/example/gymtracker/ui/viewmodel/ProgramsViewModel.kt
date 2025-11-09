package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entity.Exercise
import com.example.gymtracker.data.entity.WorkoutProgram
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProgramsViewModel(private val repository: AppRepository) : ViewModel() {
    val programs = repository.getAllPrograms()

    private var currentProgramId: Long? = null

    fun loadExercises(programId: Long): Flow<List<Exercise>> {
        currentProgramId = programId
        return repository.getExercisesByProgramId(programId)
    }

    fun addProgram(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val program = WorkoutProgram(name = name)
            repository.insertProgram(program)
            onSuccess()
        }
    }

    fun updateProgram(program: WorkoutProgram) {
        viewModelScope.launch {
            repository.updateProgram(program)
        }
    }

    fun deleteProgram(program: WorkoutProgram) {
        viewModelScope.launch {
            repository.deleteProgram(program)
        }
    }

    fun addExercise(name: String, sets: Int, reps: String) {
        val programId = currentProgramId ?: return
        viewModelScope.launch {
            val exercise = Exercise(
                programId = programId,
                name = name,
                sets = sets,
                reps = reps
            )
            repository.insertExercise(exercise)
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }
}
