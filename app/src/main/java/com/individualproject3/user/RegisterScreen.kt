package com.individualproject3.user
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen that allows the user to register
 * The user must enter their email, date of birth, and password
 * Registration is currently not implemented, and the user can navigate to the login screen
 * @param navController the navigation controller
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datePickerState = rememberDatePickerState()

    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Math & Path",
            fontSize = 32.sp,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = "Account must be created by parent or guardian",
            fontSize = 14.sp,
            modifier = Modifier.padding(20.dp)
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
            value = selectedDate?.format(dateFormatter) ?: "",
            onValueChange = { },
            label = { Text("Enter your date of birth...") },
            modifier = Modifier
                .width(400.dp)
                .padding(20.dp)
                .clickable { showDatePicker = true },
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            }
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
            onClick = { navController.navigate("login_screen") },
            modifier = Modifier.width(150.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Create Account", color = Color.White)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Already have an account?",
                modifier = Modifier.padding(end = 4.dp)
            )

            Text(
                text = "Login",
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = { navController.navigate("login_screen") })
                    .padding(start = 4.dp),
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }
}