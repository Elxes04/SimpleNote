package com.elxes.simplenote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.elxes.simplenote.presentation.note_editor.NoteEditorScreen
import com.elxes.simplenote.presentation.note_list.NoteListScreen
import com.elxes.simplenote.presentation.settings.SettingsScreen

sealed class Screen(val route: String) {
    object NoteList : Screen("note_list")
    object NoteEditor : Screen("note_editor?noteId={noteId}") {
        fun passNoteId(noteId: Long = -1L): String {
            return "note_editor?noteId=$noteId"
        }
    }
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.NoteList.route
    ) {
        composable(route = Screen.NoteList.route) {
            NoteListScreen(
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEditor.passNoteId(noteId))
                },
                onAddNoteClick = {
                    navController.navigate(Screen.NoteEditor.passNoteId())
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            NoteEditorScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
