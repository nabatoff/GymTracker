package com.gymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.data.entities.Exercise
import com.gymtracker.data.entities.WorkoutProgram
import com.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgramsViewModel(private val repository: AppRepository) : ViewModel() {
    val programs = repository.getAllPrograms()

    private val _selectedProgramId = MutableStateFlow<Long?>(null)
    val selectedProgramId: StateFlow<Long?> = _selectedProgramId.asStateFlow()

    fun selectProgram(programId: Long?) {
        _selectedProgramId.value = programId
    }

    fun addProgram(name: String) {
        viewModelScope.launch {
            repository.insertProgram(WorkoutProgram(name = name))
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

    fun getExercisesForProgram(programId: Long) = repository.getExercisesForProgram(programId)

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
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
