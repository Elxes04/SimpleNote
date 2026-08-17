package com.elxes.simplenote.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsStateWithLifecycle()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLibrariesDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete all notes?") },
            text = { Text("This action cannot be undone. All your notes will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllNotes()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = themeMode,
            onModeSelected = {
                viewModel.setThemeMode(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showLibrariesDialog) {
        LibrariesDialog(onDismiss = { showLibrariesDialog = false })
    }

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { 
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionTitle("Appearance")
            
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { 
                    Text(when(themeMode) {
                        ThemeMode.SYSTEM -> "System Default"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    })
                },
                leadingContent = { Icon(Icons.Default.BrightnessMedium, contentDescription = null) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )

            ListItem(
                headlineContent = { Text("Dynamic Color") },
                supportingContent = { Text("Use system colors (Android 12+)") },
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = dynamicColorEnabled,
                        onCheckedChange = viewModel::setDynamicColorEnabled
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionTitle("Data Management")

            ListItem(
                headlineContent = { Text("Clear All Data", color = MaterialTheme.colorScheme.error) },
                supportingContent = { Text("Permanently delete all your notes") },
                leadingContent = { 
                    Icon(
                        Icons.Default.DeleteForever, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    ) 
                },
                modifier = Modifier.clickable { showDeleteDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionTitle("About")

            ListItem(
                headlineContent = { Text("Creator") },
                supportingContent = { Text("Elxes") },
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.clickable { 
                    uriHandler.openUri("https://github.com/Elxes04")
                }
            )

            ListItem(
                headlineContent = { Text("GitHub Repository") },
                supportingContent = { Text("Source code for SimpleNote") },
                leadingContent = { Icon(Icons.Default.Code, contentDescription = null) },
                modifier = Modifier.clickable { 
                    uriHandler.openUri("https://github.com/Elxes04/SimpleNote")
                }
            )

            ListItem(
                headlineContent = { Text("Open Source Libraries") },
                supportingContent = { Text("Credits for components used in the app") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                modifier = Modifier.clickable { showLibrariesDialog = true }
            )

            ListItem(
                headlineContent = { Text("App Version") },
                supportingContent = { Text("1.0.2") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                ThemeDialogOption("System Default", currentMode == ThemeMode.SYSTEM) { onModeSelected(ThemeMode.SYSTEM) }
                ThemeDialogOption("Light", currentMode == ThemeMode.LIGHT) { onModeSelected(ThemeMode.LIGHT) }
                ThemeDialogOption("Dark", currentMode == ThemeMode.DARK) { onModeSelected(ThemeMode.DARK) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ThemeDialogOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun LibrariesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open Source Libraries") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                LibraryItem("Jetpack Compose", "Apache 2.0")
                LibraryItem("Dagger Hilt", "Apache 2.0")
                LibraryItem("Room Database", "Apache 2.0")
                LibraryItem("Jetpack DataStore", "Apache 2.0")
                LibraryItem("Compose Richtext", "MIT")
                LibraryItem("Commonmark", "BSD-2-Clause")
                LibraryItem("Splashscreen API", "Apache 2.0")
                LibraryItem("Material Components", "Apache 2.0")
                LibraryItem("Kotlin Coroutines", "Apache 2.0")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun LibraryItem(name: String, license: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = name, style = MaterialTheme.typography.titleSmall)
        Text(
            text = license,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
