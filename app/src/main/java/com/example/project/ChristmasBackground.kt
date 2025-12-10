package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.project.snow.Snowfall

@Composable
fun ChristmasBackground(
    modifier: Modifier = Modifier,
    flakesCount: Int = 120,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1B2B), // тёмный верх
                        Color(0xFF123247), // средний
                        Color(0xFF183B52)  // низ
                    )
                )
            )
    ) {
        // снег поверх фона, но *до* content — чтобы контент оказался сверху и оставался интерактивным
        Snowfall(flakesCount = flakesCount, modifier = Modifier.fillMaxSize())

        // основной контент экрана
        content()
    }
}
