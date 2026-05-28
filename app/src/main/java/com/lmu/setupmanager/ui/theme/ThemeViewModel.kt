package com.lmu.setupmanager.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.preferences.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThemeState(
    val isDarkTheme: Boolean = false,
    val useSystemTheme: Boolean = true
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = combine(
        themePreferences.isDarkTheme,
        themePreferences.useSystemTheme
    ) { isDark, useSystem ->
        ThemeState(isDarkTheme = isDark, useSystemTheme = useSystem)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ThemeState()
    )

    fun toggleDarkTheme() {
        viewModelScope.launch {
            val current = themeState.value
            themePreferences.setUseSystemTheme(false)
            themePreferences.setDarkTheme(!current.isDarkTheme)
        }
    }

    fun setUseSystemTheme(useSystem: Boolean) {
        viewModelScope.launch {
            themePreferences.setUseSystemTheme(useSystem)
        }
    }
}