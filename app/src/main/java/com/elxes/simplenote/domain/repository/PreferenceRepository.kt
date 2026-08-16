package com.elxes.simplenote.domain.repository

import com.elxes.simplenote.presentation.settings.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface PreferenceRepository {
    val themeMode: StateFlow<ThemeMode>
    val dynamicColorEnabled: StateFlow<Boolean>
    val compactSearchEnabled: StateFlow<Boolean>
    fun setThemeMode(mode: ThemeMode)
    fun setDynamicColorEnabled(enabled: Boolean)
    fun setCompactSearchEnabled(enabled: Boolean)
}
