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
import org.json.JSONObject
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.utils.NetworkUtils
import java.net.URL

@HiltViewModel
class MainViewmodel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
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


    suspend fun fetchQuote(context: Context): String {
        return try {
            if (NetworkUtils.isOnline(context)) {
                val response = URL("https://api.quotable.io/random").readText()
                val json = JSONObject(response)
                json.getString("content")
            } else {
                "Stay strong, stay private."
            }
        } catch (e: Exception) {
            "Keep your thoughts secure."
        }
    }

    fun mockNotes(): List<NoteModel> = listOf(
        NoteModel("1", "Shopping List", "Buy milk, bread, and eggs", "2025-10-31 10:00 AM"),
        NoteModel("2", "Ideas", "Implement biometric lock on edit screen", "2025-10-30 8:00 PM"),
        NoteModel("3", "Passwords", "WiFi: MySecret@123", "2025-10-28 3:45 PM")
    )

}