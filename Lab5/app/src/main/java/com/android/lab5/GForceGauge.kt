package com.android.lab5

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun GForceGauge(current: GForceSnapshot, modifier: Modifier = Modifier) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    val labelPaint = remember(onSurface) {
        Paint().apply {
            color = onSurface.copy(alpha = 0.6f).toArgb()
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.LEFT
        }
    }

    val magnitudePaint = remember(onSurface) {
        Paint().apply {
            color = onSurface.toArgb()
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .padding(24.dp)
    ) {
        val gaugeRadius = size.minDimension / 2
        val center = Offset(size.width / 2, gaugeRadius)

        // Background circle
        drawCircle(
            color = surfaceVariant,
            radius = gaugeRadius,
            center = center
        )

        labelPaint.textSize = 12.dp.toPx()

        // Concentric reference circles at 1G, 2G, 3G
        for (g in 1..SensorConfig.GAUGE_MAX_G.toInt()) {
            val r = gaugeRadius * (g / SensorConfig.GAUGE_MAX_G)
            drawCircle(
                color = outlineColor.copy(alpha = 0.25f),
                radius = r,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            drawContext.canvas.nativeCanvas.drawText(
                "${g}G",
                center.x + 4.dp.toPx(),
                center.y - r + 14.dp.toPx(),
                labelPaint
            )
        }

        // Crosshair lines
        drawLine(
            outlineColor.copy(alpha = 0.15f),
            Offset(center.x, center.y - gaugeRadius),
            Offset(center.x, center.y + gaugeRadius),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            outlineColor.copy(alpha = 0.15f),
            Offset(center.x - gaugeRadius, center.y),
            Offset(center.x + gaugeRadius, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Center dot
        drawCircle(
            color = outlineColor.copy(alpha = 0.3f),
            radius = 3.dp.toPx(),
            center = center
        )

        // Map G-force X,Y to pixel position
        val scale = gaugeRadius / SensorConfig.GAUGE_MAX_G
        val dotX = center.x + current.x * scale
        val dotY = center.y - current.y * scale

        // Clamp dot to circle boundary
        val dx = dotX - center.x
        val dy = dotY - center.y
        val dist = sqrt(dx * dx + dy * dy)
        val (finalX, finalY) = if (dist > gaugeRadius) {
            val ratio = gaugeRadius / dist
            Pair(center.x + dx * ratio, center.y + dy * ratio)
        } else {
            Pair(dotX, dotY)
        }

        // Dot color based on magnitude
        val dotColor = when {
            current.magnitude < 1.2f -> Color(0xFF4CAF50)
            current.magnitude < 2.0f -> Color(0xFFFFC107)
            else -> Color(0xFFF44336)
        }

        // Glow
        drawCircle(
            color = dotColor.copy(alpha = 0.3f),
            radius = 18.dp.toPx(),
            center = Offset(finalX, finalY)
        )

        // Main dot
        drawCircle(
            color = dotColor,
            radius = 10.dp.toPx(),
            center = Offset(finalX, finalY)
        )

        // Magnitude text below the gauge circle, inside Canvas bounds
        magnitudePaint.textSize = 18.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
            "%.2f G".format(current.magnitude),
            center.x,
            center.y + gaugeRadius + 24.dp.toPx(),
            magnitudePaint
        )
    }
}
