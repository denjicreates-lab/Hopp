package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HoppDarkColorScheme = darkColorScheme(
    primary = HoppAccentPrimary,
    onPrimary = Color.White,
    secondary = HoppAccentSecondary,
    onSecondary = Color.White,
    tertiary = HoppAccentTertiary,
    background = HoppDarkBackground,
    onBackground = HoppTextPrimary,
    surface = HoppDarkSurface,
    onSurface = HoppTextPrimary,
    surfaceVariant = HoppDarkSurfaceElevated,
    onSurfaceVariant = HoppTextSecondary,
    error = HoppError
)

private val HoppLightColorScheme = lightColorScheme(
    primary = HoppAccentPrimary,
    onPrimary = Color.White,
    secondary = HoppAccentSecondary,
    onSecondary = Color.White,
    tertiary = HoppAccentTertiary,
    background = Color(0xFFF7F7FA),
    onBackground = Color(0xFF111114),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111114),
    surfaceVariant = Color(0xFFECECEF),
    onSurfaceVariant = Color(0xFF555560),
    error = HoppError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default Hopp experience is luxury dark
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) HoppDarkColorScheme else HoppLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
