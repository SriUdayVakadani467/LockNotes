package uk.ac.tees.mad.locknote.screens

import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.locknote.R
import uk.ac.tees.mad.locknote.ui.theme.AppBackground

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    //val auth = FirebaseAuth.getInstance()

    val scale = remember { Animatable(0f) }
    var quote by remember { mutableStateOf("Keep your thoughts secure.") }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = { OvershootInterpolator(2f).getInterpolation(it) })
        )


        delay(2000)

//        val user = auth.currentUser
//        if (user != null) {
//            navController.navigate("fingerprint") {
//                popUpTo("splash") { inclusive = true }
//            }
//        } else {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("LockNotes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "\"$quote\"",
                fontSize = 14.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 32.dp),
                lineHeight = 18.sp
            )
        }
    }
}
