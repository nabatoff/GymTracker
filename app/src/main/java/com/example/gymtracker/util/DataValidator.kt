package com.example.gymtracker.util

object DataValidator {
    fun validateWeight(weight: String): ValidationResult {
        if (weight.isBlank()) {
            return ValidationResult(false, "Вес не может быть пустым")
        }
        val weightValue = weight.toFloatOrNull()
        return when {
            weightValue == null -> ValidationResult(false, "Введите корректное число")
            weightValue <= 0 -> ValidationResult(false, "Вес должен быть больше 0")
            weightValue > 500 -> ValidationResult(false, "Вес не может быть больше 500 кг")
            else -> ValidationResult(true)
        }
    }

    fun validateBodyFat(bodyFat: String?): ValidationResult {
        if (bodyFat.isNullOrBlank()) return ValidationResult(true)
        val value = bodyFat.toFloatOrNull()
        return when {
            value == null -> ValidationResult(false, "Введите корректное число")
            value < 0 -> ValidationResult(false, "Процент жира не может быть отрицательным")
            value > 100 -> ValidationResult(false, "Процент жира не может быть больше 100%")
            else -> ValidationResult(true)
        }
    }

    fun validateMuscleMass(muscleMass: String?): ValidationResult {
        if (muscleMass.isNullOrBlank()) return ValidationResult(true)
        val value = muscleMass.toFloatOrNull()
        return when {
            value == null -> ValidationResult(false, "Введите корректное число")
            value <= 0 -> ValidationResult(false, "Мышечная масса должна быть больше 0")
            value > 200 -> ValidationResult(false, "Мышечная масса не может быть больше 200 кг")
            else -> ValidationResult(true)
        }
    }

    fun validateProgramName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Название программы не может быть пустым")
            name.length > 50 -> ValidationResult(false, "Название слишком длинное (макс. 50 символов)")
            else -> ValidationResult(true)
        }
    }

    fun validateExerciseName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Название упражнения не может быть пустым")
            name.length > 100 -> ValidationResult(false, "Название слишком длинное (макс. 100 символов)")
            else -> ValidationResult(true)
        }
    }

    fun validateSets(sets: String): ValidationResult {
        if (sets.isBlank()) {
            return ValidationResult(false, "Количество подходов не может быть пустым")
        }
        val setsValue = sets.toIntOrNull()
        return when {
            setsValue == null -> ValidationResult(false, "Введите корректное число")
            setsValue <= 0 -> ValidationResult(false, "Количество подходов должно быть больше 0")
            setsValue > 50 -> ValidationResult(false, "Количество подходов не может быть больше 50")
            else -> ValidationResult(true)
        }
    }

    fun validateReps(reps: String): ValidationResult {
        return when {
            reps.isBlank() -> ValidationResult(false, "Повторения не могут быть пустыми")
            reps.length > 20 -> ValidationResult(false, "Строка повторений слишком длинная")
            else -> ValidationResult(true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
