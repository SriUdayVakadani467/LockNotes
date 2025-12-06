package uk.ac.tees.mad.locknote.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.locknote.MainViewmodel
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.screens.components.QuoteBanner
import uk.ac.tees.mad.locknote.screens.components.SwipeToRefreshList
import uk.ac.tees.mad.locknote.ui.theme.AppBackground
import uk.ac.tees.mad.locknote.ui.theme.PrimaryBlue
import uk.ac.tees.mad.locknote.ui.theme.TextWhite
import uk.ac.tees.mad.locknote.utils.BiometricUtils


@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesDashboardScreen(navController: NavController, viewmodel: MainViewmodel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var notes by remember { mutableStateOf(listOf<NoteModel>()) }
    var quote by remember { mutableStateOf("Loading your inspiration...") }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            quote = viewmodel.fetchQuote(context)
            notes = viewmodel.fetchNotes(context)
        }
    }
    val activity = LocalContext.current as FragmentActivity

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isBiometricEnabled = prefs.getBoolean("biometric_enabled", false)

        if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
            BiometricUtils.showBiometricPrompt(
                activity = activity,
                onSuccess = {
                    Toast.makeText(context, "Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    (context as Activity).finish() // optional: exit app if failed
                }
            )
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addEditNote") },
                containerColor = PrimaryBlue,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LockNotes",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Profile",
                            tint = TextWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                QuoteBanner(quote)
                Spacer(modifier = Modifier.height(16.dp))

                SwipeToRefreshList(
                    notes = notes,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            quote = viewmodel.fetchQuote(context)
                            notes = viewmodel.fetchNotes(context)
                            isRefreshing = false
                        }
                    },
                    navController = navController,
                    isRefreshing = isRefreshing
                )
            }
        }
    }
}

