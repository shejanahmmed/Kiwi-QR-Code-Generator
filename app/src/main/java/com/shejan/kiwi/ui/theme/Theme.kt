package com.shejan.kiwi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * The default dark color scheme for the Kiwi application.
 * Configured specifically for AMOLED displays using pure black backgrounds.
 */
private val DarkColorScheme = darkColorScheme(
    primary = KiwiGreen,
    secondary = DarkGrey,
    tertiary = AshGrey,
    background = AmoledBlack,
    surface = DarkGrey,
    onPrimary = AmoledBlack,
    onSecondary = White,
    onTertiary = AmoledBlack,
    onBackground = White,
    onSurface = White
)

/**
 * The core theme composable for the Kiwi application.
 * Applies the [DarkColorScheme] and custom typography to the surrounding content.
 * Note: Kiwi is designed with a forced dark theme to maintain its premium aesthetic.
 * 
 * @param content The composable content to be themed.
 */
@Composable
fun KiwiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}