package com.elxes.simplenote.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elxes.simplenote.domain.repository.NoteRepository
import com.elxes.simplenote.domain.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {
    val themeMode = preferenceRepository.themeMode
    val dynamicColorEnabled = preferenceRepository.dynamicColorEnabled
    val compactSearchEnabled = preferenceRepository.compactSearchEnabled

    fun setThemeMode(mode: ThemeMode) {
        preferenceRepository.setThemeMode(mode)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        preferenceRepository.setDynamicColorEnabled(enabled)
    }

    fun setCompactSearchEnabled(enabled: Boolean) {
        preferenceRepository.setCompactSearchEnabled(enabled)
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            noteRepository.deleteAllNotes()
        }
    }
}
