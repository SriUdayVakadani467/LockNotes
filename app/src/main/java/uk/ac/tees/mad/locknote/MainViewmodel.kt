package uk.ac.tees.mad.locknote

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewmodel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    val isLoading = mutableStateOf(false)

    fun login(context: Context, email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun signup(context: Context, email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            firebaseAuth.createUserWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}