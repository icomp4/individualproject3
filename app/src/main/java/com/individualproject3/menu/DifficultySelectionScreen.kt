package com.individualproject3.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DifficultySelectionScreen(
    navController: NavController,
    gameScreen: String,
) {
    val prettyGameName = gameScreen.getGamePretty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Math & Path",
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        Text(
            text = "$prettyGameName selected,\nplease select a difficulty",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    navController.navigate("$gameScreen/easy")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Easy", color = Color.White)
            }

            Button(
                onClick = {
                    navController.navigate("$gameScreen/medium")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Medium", color = Color.White)
            }

            Button(
                onClick = {
                    navController.navigate("$gameScreen/hard")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Hard", color = Color.White)
            }
        }
    }
}

fun String.getGamePretty(): String {
    return when (this) {
        "math_matching" -> "Math Matching"
        "maze_game" -> "Maze Game"
        else -> "Game"
    }
}