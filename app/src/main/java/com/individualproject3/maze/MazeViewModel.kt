package com.individualproject3.maze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the MazeGameContainer.
 * This ViewModel is responsible for keeping track of the game state and handling user input.
 * It is also responsible for determining the map to display based on the user's difficulty selection.
 * It also keeps track of the user's current move count and whether the game has been completed.
 */
class MazeViewModel(
    private val difficulty: String,
    private val soundManager: MazeSoundManager
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

    /**
     * The different maps for each difficulty level.
     * The maps are represented as a 2D list of CellType.
     * A cell can be either EMPTY, WALL, or GOAL.
     */
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

    /**
     * Moves the player in the specified direction.
     * If the move is valid, the player's position is updated.
     * If the player reaches the goal, the game is marked as completed.
     * Plays the appropriate sound based on the move.
     */
    fun movePlayer(direction: Direction) {
        val currentState = _gameState.value
        if (currentState.isCompleted) return

        val currentPosition = currentState.playerPosition
        val newPosition = calculateNewPosition(currentPosition, direction)

        if (isValidMove(newPosition, currentState.grid)) {
            _gameState.update { state ->
                val reachedGoal = isGoalReached(newPosition, state.grid)
                if (reachedGoal) {
                    soundManager.playCompletionSound()
                } else {
                    soundManager.playMoveSound()
                }
                state.copy(
                    playerPosition = newPosition,
                    moveCount = state.moveCount + 1,
                    isCompleted = reachedGoal
                )
            }
        } else {
            soundManager.playInvalidMoveSound()
        }
    }

    /**
     * Calculates the new position based on the current position and the direction to move.
     * The new position is calculated by moving one step in the specified direction.
     * @param current the current position of the player
     * @param direction the direction in which to move
     */
    private fun calculateNewPosition(current: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        return when (direction) {
            Direction.UP -> Pair(current.first - 1, current.second)
            Direction.DOWN -> Pair(current.first + 1, current.second)
            Direction.LEFT -> Pair(current.first, current.second - 1)
            Direction.RIGHT -> Pair(current.first, current.second + 1)
        }
    }

    /**
     * Checks if the new position is a valid move.
     * A move is considered valid if the new position is within the bounds of the grid
     * and the cell at the new position is not a wall.
     * @param position the new position to check
     * @param grid the grid representing the maze
     */
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

/**
 * Enum class representing the state of a maze game
 */
data class MazeGameState(
    val grid: List<List<CellType>> = emptyList(),
    val playerPosition: Pair<Int, Int> = Pair(0, 0),
    val moveCount: Int = 0,
    val isCompleted: Boolean = false
)

/**
 * MazeViewModelFactory class to create an instance of MazeViewModel with the specified difficulty level.
 * This class is used by the ViewModelProvider to create the ViewModel instance.
 */
class MazeViewModelFactory(
    private val difficulty: String,
    private val soundManager: MazeSoundManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MazeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MazeViewModel(difficulty, soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}