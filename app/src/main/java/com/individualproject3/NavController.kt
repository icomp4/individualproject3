package com.individualproject3


import com.individualproject3.user.LoginScreen
import com.individualproject3.math.MathMatchingScreen
import com.individualproject3.user.RegisterScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.individualproject3.maze.MazeGameContainer
import com.individualproject3.menu.DifficultySelectionScreen
import com.individualproject3.menu.GameSelection

/**
 * The navigation controller of the app
 * Determines the starting screen of the app, and the screens that can be navigated to
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login_screen"){
        composable("login_screen"){
            LoginScreen(navController = navController)
        }
        composable("register_screen"){
            RegisterScreen(navController)
        }
        composable("game_selection"){
            GameSelection(navController)
        }
        composable(
            "difficulty_selection/{gameScreen}",
            arguments = listOf(navArgument("gameScreen") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameScreen = backStackEntry.arguments?.getString("gameScreen") ?: ""
            DifficultySelectionScreen(navController, gameScreen)
        }

        composable("math_matching/{difficulty}") { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            MathMatchingScreen(navController, difficulty)
        }

        composable("maze_game/{difficulty}") { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
            MazeGameContainer(difficulty = difficulty, navController = navController)
        }
    }
}
