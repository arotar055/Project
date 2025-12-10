package com.example.project.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 🎄 Новогодние цвета

// Светлая тема
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB71C1C),      // красный
    secondary = Color(0xFF1B5E20),    // зелёный
    tertiary = Color(0xFFFFD54F)      // золотой
)

// Тёмная тема
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A80),      // светло‑красный
    secondary = Color(0xFF81C784),    // светло‑зелёный
    tertiary = Color(0xFFFFF59D)      // мягкое золото
)

@Composable
fun ProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),   // ← переключатель темы
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,   // ← твой файл Typography.kt
        content = content
    )
}
