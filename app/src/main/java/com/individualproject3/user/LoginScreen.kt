package com.individualproject3.user
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Screen that allows the user to login
 * Logging in is currently not implemented, and the user can navigate to the game selection screen
 * @param navController the navigation controller
 */
@Composable
fun LoginScreen(navController: NavController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Math & Path", fontSize = 32.sp, modifier = Modifier.padding(20.dp)

        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Enter your password...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
        )

        Button(
            onClick = {
                navController.navigate("game_selection")

            },
            modifier = Modifier
                .width(150.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)

        ) {
            Text("Login", color = Color.White)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "Don't have an account?", modifier = Modifier.padding(end = 4.dp))

            Text(
                text = "Register",
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = { navController.navigate("register_screen") })
                    .padding(start = 4.dp),
            )
        }
    }
}

