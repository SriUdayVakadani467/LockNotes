package uk.ac.tees.mad.locknote.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.locknote.model.NoteModel
import uk.ac.tees.mad.locknote.ui.theme.CardBackground
import uk.ac.tees.mad.locknote.ui.theme.TextGray
import uk.ac.tees.mad.locknote.ui.theme.TextWhite

@Composable
fun NoteCard(note: NoteModel, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.content.take(80) + if (note.content.length > 80) "..." else "",
                color = TextGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = note.timestamp,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
