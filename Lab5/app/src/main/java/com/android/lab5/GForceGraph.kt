package com.android.lab5

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.android.lab5.ui.theme.AxisXColor
import com.android.lab5.ui.theme.AxisYColor
import com.android.lab5.ui.theme.AxisZColor

@Composable
fun GForceGraph(
    history: List<GForceSnapshot>,
    modifier: Modifier = Modifier
) {
    val magnitudeColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val onSurface = MaterialTheme.colorScheme.onSurface

    val labelPaint = remember(onSurface) {
        Paint().apply {
            color = onSurface.copy(alpha = 0.5f).toArgb()
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.RIGHT
        }
    }

    val xPath = remember { Path() }
    val yPath = remember { Path() }
    val zPath = remember { Path() }
    val magnitudePath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(start = 32.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        val w = size.width
        val h = size.height
        val maxG = SensorConfig.GAUGE_MAX_G

        labelPaint.textSize = 10.dp.toPx()

        // Horizontal grid lines for magnitude (0G to 3G)
        for (g in 0..maxG.toInt()) {
            val y = h - (g / maxG) * h
            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            drawContext.canvas.nativeCanvas.drawText(
                "${g}G",
                -4.dp.toPx(),
                y + 4.dp.toPx(),
                labelPaint
            )
        }

        if (history.size < 2) return@Canvas

        val newestTime = history.last().timestamp
        val oldestTime = newestTime - SensorConfig.HISTORY_DURATION_MS

        fun timeToX(timestamp: Long): Float =
            ((timestamp - oldestTime).toFloat() / SensorConfig.HISTORY_DURATION_MS) * w

        fun magnitudeToY(value: Float): Float =
            h - (value.coerceIn(0f, maxG) / maxG) * h

        // Draw axis lines (X, Y, Z) as absolute values
        fun buildAxisPath(path: Path, selector: (GForceSnapshot) -> Float) {
            path.reset()
            history.forEachIndexed { index, snapshot ->
                val x = timeToX(snapshot.timestamp)
                val y = magnitudeToY(kotlin.math.abs(selector(snapshot)))
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
        }

        buildAxisPath(xPath) { it.x }
        buildAxisPath(yPath) { it.y }
        buildAxisPath(zPath) { it.z }

        drawPath(xPath, AxisXColor.copy(alpha = 0.4f), style = Stroke(width = 1.dp.toPx()))
        drawPath(yPath, AxisYColor.copy(alpha = 0.4f), style = Stroke(width = 1.dp.toPx()))
        drawPath(zPath, AxisZColor.copy(alpha = 0.4f), style = Stroke(width = 1.dp.toPx()))

        // Draw magnitude line (main, thicker)
        magnitudePath.reset()
        history.forEachIndexed { index, snapshot ->
            val x = timeToX(snapshot.timestamp)
            val y = magnitudeToY(snapshot.magnitude)
            if (index == 0) magnitudePath.moveTo(x, y) else magnitudePath.lineTo(x, y)
        }
        drawPath(
            magnitudePath,
            magnitudeColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
