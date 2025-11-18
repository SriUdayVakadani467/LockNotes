package uk.ac.tees.mad.locknote.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
            notes = viewmodel.mockNotes()
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
                            notes = viewmodel.mockNotes()
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

