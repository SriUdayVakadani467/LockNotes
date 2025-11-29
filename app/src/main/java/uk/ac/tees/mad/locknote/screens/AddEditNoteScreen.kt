package uk.ac.tees.mad.locknote.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.locknote.MainViewmodel
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.ui.theme.AppBackground
import uk.ac.tees.mad.locknote.ui.theme.PrimaryBlue
import uk.ac.tees.mad.locknote.ui.theme.TextGray
import uk.ac.tees.mad.locknote.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteId: String? = null,
    initialTitle: String = "",
    initialContent: String = "",
    viewmodel: MainViewmodel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoading by viewmodel.isLoading

    var title by remember { mutableStateOf(TextFieldValue(initialTitle)) }
    var content by remember { mutableStateOf(TextFieldValue(initialContent)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (noteId == null) "Add Note" else "Edit Note",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (noteId != null) {
                        IconButton(onClick = {
                            scope.launch {
                                viewmodel.deleteNote(context, noteId)
                                navController.popBackStack()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (title.text.isBlank() || content.text.isBlank()) return@FloatingActionButton
                    scope.launch {
                        viewmodel.saveOrUpdateNote(context, noteId, title.text, content.text)
                        navController.popBackStack()
                    }
                },
                containerColor = PrimaryBlue,
                contentColor = TextWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Note")
            }
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.text.isEmpty()) {
                        Text("Title...", color = TextGray, fontSize = 22.sp)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(color = TextWhite, fontSize = 16.sp, lineHeight = 22.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false)
                    .padding(bottom = 60.dp),
                decorationBox = { innerTextField ->
                    if (content.text.isEmpty()) {
                        Text("Start writing your note...", color = TextGray, fontSize = 16.sp)
                    }
                    innerTextField()
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}

