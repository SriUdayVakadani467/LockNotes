package uk.ac.tees.mad.locknote.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.locknote.MainViewmodel
import uk.ac.tees.mad.locknote.ui.theme.AppBackground
import uk.ac.tees.mad.locknote.ui.theme.PrimaryBlue
import uk.ac.tees.mad.locknote.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewmodel: MainViewmodel = hiltViewModel()) {
    val context = LocalContext.current
    var fingerprintEnabled by remember { mutableStateOf(viewmodel.getFingerprintPreference(context)) }
    var darkThemeEnabled by remember { mutableStateOf(viewmodel.getThemePreference(context)) }

    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "1.0"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", color = TextWhite, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingSwitchItem(
                icon = Icons.Default.Fingerprint,
                title = "Enable Fingerprint Unlock",
                checked = fingerprintEnabled,
                onCheckedChange = {
                    fingerprintEnabled = it
                    viewmodel.setFingerprintPreference(context, it)
                }
            )

            Spacer(Modifier.height(12.dp))

            SettingActionItem(
                icon = Icons.Default.DeleteSweep,
                title = "Clear Local Cache",
                onClick = { viewmodel.clearLocalCache(context) },
                color = Color.Red
            )

            Spacer(Modifier.height(12.dp))

            SettingActionItem(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = { viewmodel.logout(context, navController) },
                color = PrimaryBlue
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "LockNote v$versionName — Secure Notes by MAD Teesside",
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
    }
}


@Composable
fun SettingSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue)
            Spacer(Modifier.width(12.dp))
            Text(title, color = TextWhite, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
        )
    }
}

@Composable
fun SettingActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    color: Color = PrimaryBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(Modifier.width(12.dp))
        Text(title, color = color, fontWeight = FontWeight.Medium)
    }
}
