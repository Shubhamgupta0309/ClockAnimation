package com.project.clockanimation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedClock() {
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition()
    val handRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val slowHandRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val waveAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val barsAnimation = List(360) { index ->
        infiniteTransition.animateFloat(
            initialValue = with(density) { 8.dp.toPx() },
            targetValue = with(density) { (16 + 12 * (index % 3)).dp.toPx() },
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500 + (index % 60) * 100,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val brush = Brush.sweepGradient(
        0.0f to Color(0xFF9C27B0),
        0.25f to Color(0xFFE91E63),
        0.5f to Color(0xFF2196F3),
        0.75f to Color(0xFF4CAF50),
        1.0f to Color(0xFF9C27B0)
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.Black, shape = CircleShape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val clockRadius = size.minDimension / 2f - with(density) { 16.dp.toPx() }
            val numBars = 360
            val barStartRadius = clockRadius + with(density) { 8.dp.toPx() }

            val waveOffset = waveAnimation.value

            for (i in 0 until numBars) {
                val angle = (i * (2 * PI.toFloat())) / numBars
                val waveHeight = sin(waveOffset + angle) * 10
                val barHeight = barsAnimation[i].value + waveHeight
                val startX = center.x + barStartRadius * cos(angle)
                val startY = center.y + barStartRadius * sin(angle)
                val endX = center.x + (barStartRadius + barHeight) * cos(angle)
                val endY = center.y + (barStartRadius + barHeight) * sin(angle)

                drawLine(
                    brush = brush,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = (2 * PI * barStartRadius / numBars + 2).toFloat(),
                    cap = StrokeCap.Round
                )
            }

            rotate(handRotation) {
                drawLine(
                    color = Color.Blue,
                    start = center,
                    end = Offset(center.x, center.y - clockRadius + with(density) { 20.dp.toPx() }),
                    strokeWidth = with(density) { 2.dp.toPx() },
                    cap = StrokeCap.Round
                )
            }

            rotate(slowHandRotation) {
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(center.x, center.y - clockRadius / 2 + with(density) { 10.dp.toPx() }),
                    strokeWidth = with(density) { 2.dp.toPx() },
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedClockPreview() {
    AnimatedClock()
}
