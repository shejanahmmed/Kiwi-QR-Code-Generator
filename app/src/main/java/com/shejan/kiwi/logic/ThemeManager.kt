package com.shejan.kiwi.logic

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the persistent state of the application's visual theme.
 * Allows observing the current theme across the app via a StateFlow.
 */
object ThemeManager {
    private const val PREFS_NAME = "kiwi_theme_prefs"
    private const val KEY_THEME = "selected_theme"

    private lateinit var prefs: SharedPreferences
    
    // Default to "Dark Mode" to match original app design before user choice is made
    private val _themeFlow = MutableStateFlow("Dark Mode")
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()

    /**
     * Initializes the ThemeManager by reading the persisted value from SharedPreferences.
     * Should be called in Application or MainActivity onCreate.
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = prefs.getString(KEY_THEME, "System") ?: "System"
        _themeFlow.value = savedTheme
    }

    /**
     * Updates the current theme and persists it to SharedPreferences.
     */
    fun setTheme(theme: String) {
        _themeFlow.value = theme
        prefs.edit().putString(KEY_THEME, theme).apply()
    }
}
