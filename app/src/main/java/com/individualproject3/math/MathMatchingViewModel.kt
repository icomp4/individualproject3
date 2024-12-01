package com.individualproject3.math

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Equation(
    val num1: Int,
    val num2: Int,
    val result: Int,
    val correctOperator: String
)

class MathMatchingViewModel : ViewModel() {
    var currentEquation by mutableStateOf<Equation?>(null)
        private set

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

    fun generateEquation() {
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

    fun checkAnswer(selectedOperator: String) {
        currentEquation?.let { equation ->
            val correct = selectedOperator == equation.correctOperator
            isCorrect = correct

            if (correct) {
                currentStreak++
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }
    }
}