package uk.ac.tees.mad.locknote

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.utils.NetworkUtils
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel
class MainViewmodel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    val isLoading = mutableStateOf(false)

    private fun vibrate(context: Context, duration: Long = 100) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    }

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
                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("dashboard") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun saveOrUpdateNote(context: Context, noteId: String?, title: String, content: String) {
        viewModelScope.launch {
            isLoading.value = true
            val user = firebaseAuth.currentUser
            if (user == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                isLoading.value = false
                return@launch
            }

            val noteData = hashMapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            try {
                if (NetworkUtils.isOnline(context)) {
                    val notesRef = firebaseFirestore.collection("users")
                        .document(user.uid)
                        .collection("notes")

                    if (noteId == null) {
                        notesRef.add(noteData).await()
                        Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        notesRef.document(noteId).set(noteData).await()
                        Toast.makeText(context, "Note updated!", Toast.LENGTH_SHORT).show()
                    }

                    vibrate(context)
                    cacheNotes(context, fetchNotesFromFirestore(context))
                } else {
                    Toast.makeText(context, "Offline — saved locally for sync", Toast.LENGTH_SHORT).show()
                    saveDraftLocally(context, title, content)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("SaveNote", e.message.toString())
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteNote(context: Context, noteId: String) {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                firebaseFirestore.collection("users")
                    .document(user.uid)
                    .collection("notes")
                    .document(noteId)
                    .delete()
                    .await()

                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                vibrate(context, 70)
                cacheNotes(context, fetchNotesFromFirestore(context))
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun fetchNotes(context: Context): List<NoteModel> {
        return if (NetworkUtils.isOnline(context)) {
            val notes = fetchNotesFromFirestore(context)
            cacheNotes(context, notes)
            notes
        } else {
            fetchNotesFromCache(context)
        }
    }

    private suspend fun fetchNotesFromFirestore(context: Context): List<NoteModel> {
        val user = firebaseAuth.currentUser ?: return emptyList()
        val snapshot = firebaseFirestore.collection("users")
            .document(user.uid)
            .collection("notes")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val title = doc.getString("title") ?: ""
            val content = doc.getString("content") ?: ""
            val time = doc.getLong("timestamp") ?: 0L
            NoteModel(
                id = doc.id,
                title = title,
                content = content,
                timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time)
            )
        }
    }

    private fun cacheNotes(context: Context, notes: List<NoteModel>) {
        val prefs = context.getSharedPreferences("cached_notes", Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (note in notes) {
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("content", note.content)
            obj.put("timestamp", note.timestamp)
            jsonArray.put(obj)
        }
        prefs.edit().putString("notes_data", jsonArray.toString()).apply()
    }

    private fun fetchNotesFromCache(context: Context): List<NoteModel> {
        val prefs = context.getSharedPreferences("cached_notes", Context.MODE_PRIVATE)
        val jsonString = prefs.getString("notes_data", null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val notes = mutableListOf<NoteModel>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            notes.add(
                NoteModel(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    content = obj.getString("content"),
                    timestamp = obj.getString("timestamp")
                )
            )
        }
        return notes
    }

    private fun saveDraftLocally(context: Context, title: String, content: String) {
        val prefs = context.getSharedPreferences("draft_notes", Context.MODE_PRIVATE)
        prefs.edit().putString(System.currentTimeMillis().toString(), "$title\n$content").apply()
    }

    fun fetchQuote(context: Context): String {
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

    fun setFingerprintPreference(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun getFingerprintPreference(context: Context): Boolean {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("biometric_enabled", false)
    }

    fun setThemePreference(context: Context, darkMode: Boolean) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dark_mode", darkMode).apply()
    }

    fun getThemePreference(context: Context): Boolean {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("dark_mode", false)
    }

    fun clearLocalCache(context: Context) {
        val prefs1 = context.getSharedPreferences("cached_notes", Context.MODE_PRIVATE)
        val prefs2 = context.getSharedPreferences("draft_notes", Context.MODE_PRIVATE)
        prefs1.edit().clear().apply()
        prefs2.edit().clear().apply()
        Toast.makeText(context, "Local cache cleared", Toast.LENGTH_SHORT).show()
    }

    fun logout(context: Context, navController: NavController) {
        firebaseAuth.signOut()
        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        navController.navigate("auth") {
            popUpTo("dashboard") { inclusive = true }
        }
    }
}
