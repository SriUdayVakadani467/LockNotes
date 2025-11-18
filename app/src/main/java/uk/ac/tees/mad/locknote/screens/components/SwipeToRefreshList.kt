package uk.ac.tees.mad.locknote.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.ui.theme.TextGray

@Composable
fun SwipeToRefreshList(
    notes: List<NoteModel>,
    onRefresh: () -> Unit,
    navController: NavController,
    isRefreshing: Boolean
) {
    Column {
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes yet. Tap + to add one!", color = TextGray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notes) { note ->
                    NoteCard(note) {
                        navController.navigate("addEditNote?noteId=${note.id}")
                    }
                }
            }
        }
    }
}
