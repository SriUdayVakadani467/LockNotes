package uk.ac.tees.mad.locknote.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.locknote.screens.components.AuthTextField
import uk.ac.tees.mad.locknote.ui.theme.AppBackground
import uk.ac.tees.mad.locknote.ui.theme.PrimaryBlue

@Composable
fun AuthScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isLogin) "Welcome Back" else "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLogin) "Sign in to your secure notes" else "Sign up to start protecting your notes",
                fontSize = 14.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(40.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            if (!isLogin) {
                Spacer(modifier = Modifier.height(16.dp))
                AuthTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || (!isLogin && confirmPassword.isBlank())) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isLogin && password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

//                    coroutineScope.launch {
//                        isLoading = true
//                        if (isLogin) {
//                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
//                                .addOnCompleteListener { task ->
//                                    isLoading = false
//                                    if (task.isSuccessful) {
//                                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
//                                        navController.navigate("dashboard") {
//                                            popUpTo("auth") { inclusive = true }
//                                        }
//                                    } else {
//                                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        } else {
//                            auth.createUserWithEmailAndPassword(email.trim(), password.trim())
//                                .addOnCompleteListener { task ->
//                                    isLoading = false
//                                    if (task.isSuccessful) {
//                                        Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
//                                        navController.navigate("dashboard") {
//                                            popUpTo("auth") { inclusive = true }
//                                        }
//                                    } else {
//                                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        }
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading)
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                else
                    Text(text = if (isLogin) "Login" else "Sign Up", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isLogin) "Don’t have an account? Sign Up" else "Already have an account? Login",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.clickable { isLogin = !isLogin }
            )
        }
    }
}
