package com.saltto.gluckskeks_recipeapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.saltto.gluckskeks_recipeapp.navigation.Routes

@Composable
fun SignUpScreen(navController: NavHostController) {
    val context = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    val trimmedPassword = password.trim()

                    if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Email and password cannot be empty.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Firebase.auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Sign Up Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    autoLogin(context, trimmedEmail, trimmedPassword){
                                        Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate(Routes.HOME) { // Navigate to Router
                                            popUpTo(Routes.SIGNUP) { inclusive = true }
                                        }
                                    }
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(Routes.SIGNUP) { inclusive = true }
                                    }
                                } else {
                                    val errorMessage = task.exception?.message ?: "Sign Up Failed"
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign Up")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SIGNUP) { inclusive = true }
                }
            }) {
                Text(text = "Already have an account? Sign In")
            }
        }
    }
}

fun autoLogin(context: Context, email: String, password: String, onSuccess: () -> Unit) {
    Firebase.auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                val errorMessage = task.exception?.message ?: "Sign In Failed"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
}