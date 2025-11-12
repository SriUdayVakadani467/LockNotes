package uk.ac.tees.mad.locknote.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController) }
//
//        composable("auth") { AuthScreen(navController) }
//
//        composable("fingerprint") { FingerprintScreen(navController) }
//
//        composable("dashboard") { NotesDashboardScreen(navController) }
//
//        composable("addEditNote") { AddEditNoteScreen(navController) }
//
//        composable("settings") { SettingsScreen(navController) }
    }
}
