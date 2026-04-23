package com.shejan.kiwi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
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
    surfaceVariant = AshGrey,
    onPrimary = AmoledBlack,
    onSecondary = White,
    onTertiary = AmoledBlack,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = White
)

private val LightColorScheme = lightColorScheme(
    primary = KiwiGreen,
    secondary = LightSurface,
    tertiary = LightSurfaceVariant,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = AmoledBlack,
    onSecondary = TextDark,
    onTertiary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextDark
)

/**
 * The core theme composable for the Kiwi application.
 * Applies the appropriate color scheme and custom typography to the surrounding content.
 * 
 * @param darkTheme Whether to use the dark theme (defaults to system setting).
 * @param content The composable content to be themed.
 */
@Composable
fun KiwiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}