package com.individualproject3.maze

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

/**
 * The MazeGameContainer is responsible for instantiating the MazeScreen, along with the viewmodel
 */
@Composable
fun MazeGameContainer(
    difficulty: String,
    navController: NavController,
    viewModel: MazeViewModel = viewModel(
        factory = MazeViewModelFactory(
            difficulty = difficulty,
            soundManager = MazeSoundManager(LocalContext.current)
        )
    )
) {
    val gameState by viewModel.gameState.collectAsState()

    // If the game is over, display the completed screen
    if (gameState.isCompleted) {
        CompletionScreen(
            moveCount = gameState.moveCount,
            onReplayClick = { viewModel.resetGame() },
            onHomeClick = { navController.navigate("game_selection") }
        )
    } else {
        MazeScreen(
            gridState = gameState.grid,
            playerPosition = gameState.playerPosition,
            onDirectionClick = { direction -> viewModel.movePlayer(direction) },
            onHomeClick = { navController.navigate("game_selection") }
        )
    }
}

/**
 * The completion screen displays the amount of moves it took the player to reach the end, and buttons for replaying or going to game selection screen
 */
@Composable
fun CompletionScreen(
    moveCount: Int,
    onReplayClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Well played! You complete the map in $moveCount moves.",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onReplayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("replay")
            }

            Button(
                onClick = onHomeClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Home")
            }
        }
    }
}


/**
 * The MazeScreen composable displays the maze, and control buttons
 * It has parameters for gridState (2d list), playerPosition, onDirectionClick, and onHomeClick
 */
@Composable
fun MazeScreen(
    modifier: Modifier = Modifier,
    gridState: List<List<CellType>> = emptyList(),
    playerPosition: Pair<Int, Int> = Pair(0, 0),
    onDirectionClick: (Direction) -> Unit = {},
    onHomeClick: () -> Unit = {}
) {

    // Get the current context
    val context = LocalContext.current

    // Lock the screen landscape mode
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    gridState.forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier.padding(vertical = 0.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            row.forEachIndexed { colIndex, cell ->
                                GameCell(
                                    cellType = cell,
                                    isPlayerHere = playerPosition.first == rowIndex &&
                                            playerPosition.second == colIndex
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap the buttons to move the player!",
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                DirectionButton(
                    direction = Direction.UP,
                    onClick = { onDirectionClick(Direction.UP) }
                )
                DirectionButton(
                    direction = Direction.DOWN,
                    onClick = { onDirectionClick(Direction.DOWN) }
                )
                DirectionButton(
                    direction = Direction.LEFT,
                    onClick = { onDirectionClick(Direction.LEFT) }
                )
                DirectionButton(
                    direction = Direction.RIGHT,
                    onClick = { onDirectionClick(Direction.RIGHT) }
                )
            }

            Button(
                onClick = onHomeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Home")
            }
        }
    }
}

/**
 * The GameCell composable is used to render the tiles of the maze
 * It has parameters for cell type (empty, wall, goal), as well as if the player is in the current cell
 */
@Composable
fun GameCell(
    cellType: CellType,
    isPlayerHere: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .padding(1.dp)
            .background(
                when (cellType) {
                    CellType.EMPTY -> Color(0xFF2196F3)
                    CellType.WALL -> Color(0xFF424242)
                    CellType.GOAL -> Color.White
                }
            )
            .border(
                width = 0.5.dp,
                color = when (cellType) {
                    CellType.EMPTY -> Color.White.copy(alpha = 0.2f)
                    CellType.WALL -> Color.Black.copy(alpha = 0.3f)
                    CellType.GOAL -> Color.Gray.copy(alpha = 0.3f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (cellType) {
            CellType.WALL -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = Color.Black.copy(alpha = 0.1f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.1f),
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )
                }
            }
            CellType.GOAL -> {
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = minOf(size.width, size.height) / 4

                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.2f),
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.2f),
                        radius = radius / 2,
                        center = Offset(centerX, centerY)
                    )
                }
            }
            else -> { }
        }

        if (isPlayerHere) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(
                        Color(0xFF4CAF50),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Player",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }
    }
}

/**
 * The DirectionButton composable is used to render the directional buttons
 * Controls include up, down, left, right
 */
@Composable
fun DirectionButton(
    direction: Direction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = when (direction) {
                Direction.UP -> Icons.Default.KeyboardArrowUp
                Direction.DOWN -> Icons.Default.KeyboardArrowDown
                Direction.LEFT -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
                Direction.RIGHT -> Icons.AutoMirrored.Filled.KeyboardArrowRight
            },
            contentDescription = "Move ${direction.name.lowercase()}",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

// Enum for the available directions the player can move
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

// Enum for the types of cells available in the game
enum class CellType {
    EMPTY, WALL, GOAL
}

/**
 * Helper function for finding the current activity
 * Used when locking the screen landscape
 */
private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}