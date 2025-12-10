package com.example.project.snow

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    val x: Float,
    val size: Float,
    val speed: Float,
    val amplitude: Float,
    val phase: Float,
    val alpha: Float
)

private fun randomSnowflake(): Snowflake {
    return Snowflake(
        x = Random.nextFloat(),
        size = Random.nextFloat() * 6f + 2f,
        speed = Random.nextFloat() * 0.5f + 0.2f,
        amplitude = Random.nextFloat() * 35f + 10f,
        phase = Random.nextFloat() * 6f,
        alpha = Random.nextFloat() * 0.6f + 0.4f
    )
}

@Composable
fun Snowfall(
    flakesCount: Int,
    modifier: Modifier = Modifier
) {
    // Генерируем снежинки 1 раз
    val snowflakes = remember { List(flakesCount) { randomSnowflake() } }

    // Анимация
    val transition = rememberInfiniteTransition(label = "snow")
    val fall by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fall"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        snowflakes.forEach { flake ->
            val xOffset = flake.x * w + sin(fall * 6f + flake.phase) * flake.amplitude
            val yOffset = (fall * h * flake.speed + flake.phase * 40f) % h

            drawCircle(
                color = Color.White.copy(alpha = flake.alpha),
                radius = flake.size,
                center = Offset(xOffset, yOffset)
            )
        }
    }
}
