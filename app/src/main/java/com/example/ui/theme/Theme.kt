package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CosmicNeonCyan,
    onPrimary = CosmicDeepSpace,
    secondary = CosmicNeonMagenta,
    onSecondary = CosmicDeepSpace,
    tertiary = CosmicNeonPurple,
    onTertiary = CosmicTextPrimary,
    background = CosmicDeepSpace,
    onBackground = CosmicTextPrimary,
    surface = CosmicSurface,
    onSurface = CosmicTextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = CosmicTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors to preserve our beautiful branded Cosmic theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
