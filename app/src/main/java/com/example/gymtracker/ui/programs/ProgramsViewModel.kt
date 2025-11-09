package com.example.gymtracker.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entities.Exercise
import com.example.gymtracker.data.entities.WorkoutProgram
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProgramsUiState(
    val programs: List<WorkoutProgram> = emptyList(),
    val isLoading: Boolean = false
)

class ProgramsViewModel(private val repository: AppRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgramsUiState())
    val uiState: StateFlow<ProgramsUiState> = _uiState.asStateFlow()

    init {
        loadPrograms()
    }

    private fun loadPrograms() {
        viewModelScope.launch {
            repository.getAllPrograms().collect { programs ->
                _uiState.value = _uiState.value.copy(programs = programs)
            }
        }
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
}

data class ProgramDetailUiState(
    val program: WorkoutProgram? = null,
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false
)

class ProgramDetailViewModel(
    private val repository: AppRepository,
    private val programId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgramDetailUiState())
    val uiState: StateFlow<ProgramDetailUiState> = _uiState.asStateFlow()

    init {
        loadProgram()
        loadExercises()
    }

    private fun loadProgram() {
        viewModelScope.launch {
            val program = repository.getProgramById(programId)
            _uiState.value = _uiState.value.copy(program = program)
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            repository.getExercisesByProgramId(programId).collect { exercises ->
                _uiState.value = _uiState.value.copy(exercises = exercises)
            }
        }
    }

    fun addExercise(name: String, sets: Int, reps: String) {
        viewModelScope.launch {
            repository.insertExercise(
                Exercise(
                    programId = programId,
                    name = name,
                    sets = sets,
                    reps = reps
                )
            )
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
