package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MyraUltraDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFF8CF4FF),
    secondary = NeonMagenta,
    onSecondary = Color(0xFF3B001B),
    secondaryContainer = Color(0xFF6B0033),
    onSecondaryContainer = Color(0xFFFFB0CA),
    tertiary = CyberPurple,
    onTertiary = Color(0xFF1F0059),
    tertiaryContainer = Color(0xFF450DB8),
    onTertiaryContainer = Color(0xFFD6C6FF),
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = CyberRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve MYRA ULTRA cyber aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MyraUltraDarkColorScheme,
        typography = Typography,
        content = content
    )
}
