package com.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// -------------------------
// 🎄 Новогодняя палитра
// -------------------------

// Светлая тема — снежная
private val ChristmasLightColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),      // Рождественский красный
    secondary = Color(0xFF2E7D32),    // Ёлочный зелёный
    tertiary = Color(0xFFFFC107),     // Золото
    background = Color(0xFFF4F9FF),   // Снежный фон
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// Тёмная тема — рождественская ночь
private val ChristmasDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6659),      // Тёплый красный
    secondary = Color(0xFF80E27E),    // Светлый зелёный
    tertiary = Color(0xFFFFD54F),     // Золото
    background = Color(0xFF121212),   // Новогодняя ночь
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// -------------------------
// 🎁 Новогодняя тема
// -------------------------
@Composable
fun ProjectTheme(
    darkTheme: Boolean = false,   // можно оставить isSystemInDarkTheme(), если хочешь
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        ChristmasDarkColorScheme
    } else {
        ChristmasLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
