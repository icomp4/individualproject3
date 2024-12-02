package com.individualproject3.math
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt


/**
 * The MathMatchingScreen composable is the UI for the Math Matching game.
 * It displays an equation with a missing operator, and the user must drag the correct operator that makes the equation true.
 */
@Composable
fun MathMatchingScreen(
    navController: NavController,
    difficulty: String,
    viewModel: MathMatchingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    // State for the dragged operator
    var draggedOperator by remember { mutableStateOf<String?>(null) }

    // State for the current offset of the dragged operator
    var currentOffset by remember { mutableStateOf(Offset.Zero) }

    // State for whether the operator is being dragged
    var isDragging by remember { mutableStateOf(false) }

    // State for the position of the drop target
    var dropTargetPosition by remember { mutableStateOf(Offset.Zero) }

    val animatedOffset by animateOffsetAsState(
        targetValue = currentOffset,
        animationSpec = spring()
    )

    // Generate a new equation and set the difficulty when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.generateEquation()
        viewModel.setDifficulty(difficulty)
    }

    // Lock the screen orientation to landscape
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Drag the operation that makes the equation true",
                modifier = Modifier.padding(vertical = 16.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Current Streak: ${viewModel.currentStreak}",
                    fontSize = 16.sp
                )
                Text(
                    text = "Best Streak: ${viewModel.bestStreak}",
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("+", "-", "X", "/").forEach { operator ->
                    Spacer(modifier = Modifier.width(8.dp))
                    OperatorButton(
                        operator = operator,
                        backgroundColor = Color(0xFF2ECC71),
                        onDragStart = { offset ->
                            draggedOperator = operator
                            currentOffset = offset
                            isDragging = true
                        },
                        onDrag = { offset ->
                            currentOffset = offset
                        },
                        onDragEnd = { offset ->
                            val distance = (offset - dropTargetPosition).getDistance()
                            if (distance < 100f) {
                                viewModel.checkAnswer(operator)
                            }
                            isDragging = false
                            currentOffset = Offset.Zero
                        },
                        isDragged = draggedOperator == operator && isDragging
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                viewModel.currentEquation?.let { equation ->
                    Text("${equation.num1}", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))

                    Card(
                        modifier = Modifier
                            .size(60.dp)
                            .onGloballyPositioned { coordinates ->
                                dropTargetPosition = coordinates.positionInRoot()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when (viewModel.isCorrect) {
                                true -> Color.Green
                                false -> Color.Red
                                null -> Color.Gray
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isDragging && draggedOperator != null)
                                    draggedOperator!! else "?",
                                color = Color.White,
                                fontSize = 32.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${equation.num2}", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("=", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${equation.result}", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedContent(targetState = viewModel.isCorrect) { isCorrect ->
                when (isCorrect) {
                    false -> Text(
                        "Incorrect, try again!",
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    else -> null
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("game_selection") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Home", color = Color.White)
                }

                if (viewModel.isCorrect == true) {
                    Button(
                        onClick = {
                            viewModel.generateEquation()
                            draggedOperator = null
                            currentOffset = Offset.Zero
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Next Equation")
                    }
                }
            }
        }

        if (isDragging && draggedOperator != null) {
            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        animatedOffset.x.roundToInt(),
                        animatedOffset.y.roundToInt()
                    )
                }
            ) {
                OperatorButton(
                    operator = draggedOperator!!,
                    backgroundColor = Color(0xFF2ECC71),
                    isDragged = true
                )
            }
        }
    }
}

/**
 * The OperatorButton composable is a button that displays an operator.
 * The button can be dragged by the user to the missing operator in the equation.
 * @param operator The operator to display on the button
 * @param backgroundColor The background color of the button
 */
@Composable
private fun OperatorButton(
    operator: String,
    backgroundColor: Color,
    onDragStart: (Offset) -> Unit = { _ -> },
    onDrag: (Offset) -> Unit = { _ -> },
    onDragEnd: (Offset) -> Unit = { _ -> },
    isDragged: Boolean = false
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    Button(
        onClick = { },
        modifier = Modifier
            .size(60.dp)
            .onGloballyPositioned { coordinates ->
                if (!isDragged) {
                    currentPosition = coordinates.positionInRoot()
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { _ ->
                        onDragStart(currentPosition)
                    },
                    onDragEnd = {
                        onDragEnd(currentPosition)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentPosition += dragAmount
                        onDrag(currentPosition)
                    }
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = operator,
                color = Color.White,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }
    }
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