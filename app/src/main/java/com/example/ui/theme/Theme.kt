package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BullGreen,
    onPrimary = Color.Black,
    primaryContainer = CryptoSurfaceVariant,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = CryptoSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningAmber,
    onTertiary = Color.Black,
    background = CryptoBackground,
    onBackground = TextPrimary,
    surface = CryptoSurface,
    onSurface = TextPrimary,
    surfaceVariant = CryptoSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CryptoCardBorder,
    error = BearRed,
    onError = Color.White
)

@Composable
fun SmartTradeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
