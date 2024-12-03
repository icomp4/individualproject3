package com.individualproject3.math

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * ViewModel for the MathMatchingScreen.
 * This ViewModel is responsible for generating equations and checking if the user's answer is correct.
 * It is also responsible for determining the difficulty of the equations based on the user's selection.
 * It also keeps track of the user's current and best streaks.
 */
class MathMatchingViewModel(
    private val soundManager: MathSoundManager
) : ViewModel() {

    var currentEquation by mutableStateOf<Equation?>(null)
        private set

    private var hasGottenCorrect by mutableStateOf(false)

    var isCorrect by mutableStateOf<Boolean?>(null)
        private set

    var currentStreak by mutableIntStateOf(0)
        private set

    var bestStreak by mutableIntStateOf(0)
        private set

    private var currentDifficulty by mutableStateOf("easy")

    private val operators = listOf("+", "-", "X", "/")
    private val difficultyRanges = mapOf(
        "easy" to (1..10),
        "medium" to (1..50),
        "hard" to (1..100)
    )

    fun setDifficulty(difficulty: String) {
        currentDifficulty = difficulty
        currentStreak = 0
        bestStreak = 0
        generateEquation()
    }

    /**
     * Generates a new equation based on the current difficulty.
     * Easy difficulty ranges from 1 to 10.
     * Medium difficulty ranges from 1 to 50.
     * Hard difficulty ranges from 1 to 100.
     */
    fun generateEquation() {
        hasGottenCorrect = false
        val range = difficultyRanges[currentDifficulty] ?: difficultyRanges["easy"]!!
        val operator = operators.random()

        val (num1, num2, result) = when (operator) {
            "+" -> {
                val n1 = range.random()
                val n2 = range.random()
                Triple(n1, n2, n1 + n2)
            }
            "-" -> {
                val n1 = range.random()
                val n2 = range.random()
                val max = maxOf(n1, n2)
                val min = minOf(n1, n2)
                Triple(max, min, max - min)
            }
            "X" -> {
                val n1 = if (currentDifficulty == "easy") (1..10).random() else range.random()
                val n2 = if (currentDifficulty == "easy") (1..10).random() else range.random()
                Triple(n1, n2, n1 * n2)
            }
            "/" -> {
                val n2 = if (currentDifficulty == "easy") (1..10).random() else range.random()
                val result = if (currentDifficulty == "easy") (1..10).random() else range.random()
                val n1 = n2 * result
                Triple(n1, n2, result)
            }
            else -> Triple(0, 0, 0)
        }

        currentEquation = Equation(num1, num2, result, operator)
        isCorrect = null
    }

    /**
     * Checks if the user's answer is correct only if the equation has not been answered yet.
     * Updates the current streak and best streak accordingly.
     * Plays the correct or incorrect sound effect.
     */
    fun checkAnswer(selectedOperator: String) {
        currentEquation?.let { equation ->
            if (!hasGottenCorrect) {
                val correct = selectedOperator == equation.correctOperator
                isCorrect = correct

                if (correct) {
                    soundManager.playCorrectSound()
                    currentStreak++
                    if (currentStreak > bestStreak) {
                        bestStreak = currentStreak
                    }
                    hasGottenCorrect = true
                } else {
                    soundManager.playIncorrectSound()
                    currentStreak = 0
                }
            }
        }
    }

/**
 * Data class representing an equation.
 * @param num1 The first number in the equation.
 * @param num2 The second number in the equation.
 * @param result The result of the equation.
 * @param correctOperator The correct operator for the equation.
 */
data class Equation(
    val num1: Int,
    val num2: Int,
    val result: Int,
    val correctOperator: String
)

class MathMatchingViewModelFactory(
    private val soundManager: MathSoundManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MathMatchingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MathMatchingViewModel(soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}