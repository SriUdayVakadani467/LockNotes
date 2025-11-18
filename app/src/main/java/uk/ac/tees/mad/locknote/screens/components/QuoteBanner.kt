package uk.ac.tees.mad.locknote.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.locknote.ui.theme.CardBackground
import uk.ac.tees.mad.locknote.ui.theme.TextGray

@Composable
fun QuoteBanner(quote: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "“$quote”",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(16.dp),
            lineHeight = 18.sp
        )
    }
}
