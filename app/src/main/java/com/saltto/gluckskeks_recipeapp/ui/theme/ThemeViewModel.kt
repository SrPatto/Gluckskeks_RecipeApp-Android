package com.saltto.gluckskeks_recipeapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.saltto.gluckskeks_recipeapp.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = ThemePreferences(application)

    val isDarkTheme = prefs.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setDarkTheme(enabled)
        }
    }
}