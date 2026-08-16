package com.elxes.simplenote.data.repository

import com.elxes.simplenote.domain.repository.PreferenceRepository
import com.elxes.simplenote.presentation.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepositoryImpl @Inject constructor() : PreferenceRepository {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(true)
    override val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    override fun setDynamicColorEnabled(enabled: Boolean) {
        _dynamicColorEnabled.value = enabled
    }
}
