package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PixelDarkColorScheme = darkColorScheme(
    primary = PixelGold,
    onPrimary = Color(0xFF1B1100),
    primaryContainer = PixelSurfaceVariant,
    onPrimaryContainer = PixelGold,
    secondary = PixelEmerald,
    onSecondary = Color(0xFF002117),
    secondaryContainer = Color(0xFF13362C),
    onSecondaryContainer = PixelEmerald,
    tertiary = PixelPink,
    onTertiary = Color(0xFF370014),
    background = PixelDarkBg,
    onBackground = PixelTextPrimary,
    surface = PixelSurface,
    onSurface = PixelTextPrimary,
    surfaceVariant = PixelSurfaceVariant,
    onSurfaceVariant = PixelTextSecondary,
    outline = PixelBorderHighlight,
    outlineVariant = PixelBorderDark
)

@Composable
fun PixelLifeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PixelDarkColorScheme,
        typography = Typography,
        content = content
    )
}
