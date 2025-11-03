package com.done.designsystem.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BrandGreen = Color(0xFF40C463)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val dark = darkColorScheme(
        primary = Color(0xFF22C55E),
        onPrimary = Color.Black,
        surface = Color(0xFF12251D),
        onSurface = Color(0xFFE7F5EE),
        background = Color(0xFF1A1A1A),
        onBackground = Color(0xFFE7F5EE)
    )
    MaterialTheme(
        colorScheme = dark,
        typography = Typography(),
        content = content
    )
}

