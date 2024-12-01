package com.individualproject3.maze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MazeGameState(
    val grid: List<List<CellType>> = emptyList(),
    val playerPosition: Pair<Int, Int> = Pair(0, 0),
    val moveCount: Int = 0,
    val isCompleted: Boolean = false
)

class MazeViewModel(
    private val difficulty: String
) : ViewModel() {

    private val _gameState = MutableStateFlow(MazeGameState())
    val gameState: StateFlow<MazeGameState> = _gameState.asStateFlow()

    init {
        startNewGame()
    }

    private fun startNewGame() {
        val initialGrid = when (difficulty.lowercase()) {
            "medium" -> mediumMaze
            "hard" -> hardMaze
            else -> easyMaze
        }

        _gameState.value = MazeGameState(
            grid = initialGrid,
            playerPosition = getStartPosition(initialGrid),
            moveCount = 0,
            isCompleted = false
        )
    }

    private fun getStartPosition(grid: List<List<CellType>>): Pair<Int, Int> {
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == CellType.EMPTY) {
                    return Pair(rowIndex, colIndex)
                }
            }
        }
        return Pair(0, 0)
    }

    companion object {
        private val easyMaze = listOf(
            listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL),
            listOf(CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
        )

        private val mediumMaze = listOf(
            listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL),
            listOf(CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.GOAL)
        )

        private val hardMaze = listOf(
            listOf(CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.WALL, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.WALL),
            listOf(CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.WALL),
            listOf(CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.GOAL)
        )
    }

    fun movePlayer(direction: Direction) {
        val currentState = _gameState.value
        if (currentState.isCompleted) return

        val currentPosition = currentState.playerPosition
        val newPosition = calculateNewPosition(currentPosition, direction)

        if (isValidMove(newPosition, currentState.grid)) {
            _gameState.update { state ->
                state.copy(
                    playerPosition = newPosition,
                    moveCount = state.moveCount + 1,
                    isCompleted = isGoalReached(newPosition, state.grid)
                )
            }
        }
    }

    private fun calculateNewPosition(current: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> Pair(current.first - 1, current.second)
            Direction.DOWN -> Pair(current.first + 1, current.second)
            Direction.LEFT -> Pair(current.first, current.second - 1)
            Direction.RIGHT -> Pair(current.first, current.second + 1)
        }
    }

    private fun isValidMove(position: Pair<Int, Int>, grid: List<List<CellType>>): Boolean {
        if (position.first < 0 || position.first >= grid.size ||
            position.second < 0 || position.second >= grid[0].size) {
            return false
        }

        return grid[position.first][position.second] != CellType.WALL
    }

    private fun isGoalReached(position: Pair<Int, Int>, grid: List<List<CellType>>): Boolean {
        return grid[position.first][position.second] == CellType.GOAL
    }

    fun resetGame() {
        startNewGame()
    }

}



class MazeViewModelFactory(private val difficulty: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MazeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MazeViewModel(difficulty) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}