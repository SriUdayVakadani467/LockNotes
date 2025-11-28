package uk.ac.tees.mad.locknote.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController) }

        composable("auth") { AuthScreen(navController) }
//
//        composable("fingerprint") { FingerprintScreen(navController) }
//
        composable("dashboard") { NotesDashboardScreen(navController) }
//
        composable(
            route = "addEditNote?noteId={noteId}&noteTitle={noteTitle}&noteContent={noteContent}",
            arguments = listOf(
                navArgument("noteId") { nullable = true; defaultValue = null },
                navArgument("noteTitle") { nullable = true; defaultValue = null },
                navArgument("noteContent") { nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val noteTitle = backStackEntry.arguments?.getString("noteTitle")
            val noteContent = backStackEntry.arguments?.getString("noteContent")

            AddEditNoteScreen(
                navController = navController,
                noteId = noteId,
                initialTitle = noteTitle ?: "",
                initialContent = noteContent ?: ""
            )
        }
//
//        composable("settings") { SettingsScreen(navController) }
    }
}
