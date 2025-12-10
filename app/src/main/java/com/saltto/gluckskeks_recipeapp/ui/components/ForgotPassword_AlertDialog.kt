package com.saltto.gluckskeks_recipeapp.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ForgotPassword_AlertDialog(
    context: Context, show: Boolean,
    onShowChange: (Boolean) -> Unit
) {
    if (show) {
        var resetEmail by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            title = { Text(text = "Forgot Password") },
            text = {
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text(text = "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (resetEmail.isNotBlank()) {
                        Firebase.auth.sendPasswordResetEmail(resetEmail)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context, "Check your email to reset password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onShowChange(false)
                                } else {
                                    Toast.makeText(
                                        context, "Registered email not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context, "Please enter your registered email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(text = "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onShowChange(false)
                }) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = { onShowChange(false) }
        )

    }

}
