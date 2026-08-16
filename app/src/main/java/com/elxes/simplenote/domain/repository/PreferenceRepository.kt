package com.elxes.simplenote.domain.repository

import com.elxes.simplenote.presentation.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val themeMode: Flow<ThemeMode>
    val dynamicColorEnabled: Flow<Boolean>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorEnabled(enabled: Boolean)
}
