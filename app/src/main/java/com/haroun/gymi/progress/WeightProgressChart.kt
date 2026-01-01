package com.haroun.gymi.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.haroun.gymi.domain.model.ProgressPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeightProgressChart(
    points: List<ProgressPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val sortedPoints = points.sortedBy { it.date }
    val minWeight = sortedPoints.minOf { it.weight }
    val maxWeight = sortedPoints.maxOf { it.weight }

    Canvas(modifier = modifier.height(200.dp).padding(16.dp)) {
        val w = size.width
        val h = size.height

        val paddingLeft = 40f
        val paddingBottom = 20f

        // --- Dibujar eje Y (peso) ---
        val weightRange = maxWeight - minWeight
        val yStep = weightRange / 5f

        for (i in 0..5) {
            val yValue = minWeight + i * yStep
            val yPos = h - paddingBottom - (i * (h - paddingBottom) / 5f)
            drawContext.canvas.nativeCanvas.drawText(
                "${yValue.toInt()}kg",
                0f,
                yPos,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                }
            )
        }

        // --- Dibujar eje X (días) ---
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        val xStep = (w - paddingLeft) / (sortedPoints.size - 1).coerceAtLeast(1)
        sortedPoints.forEachIndexed { index, point ->
            val xPos = paddingLeft + index * xStep
            drawContext.canvas.nativeCanvas.drawText(
                dateFormat.format(Date(point.date)),
                xPos,
                h,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // --- Dibujar línea de peso ---
        val path = androidx.compose.ui.graphics.Path()
        sortedPoints.forEachIndexed { index, point ->
            val x = paddingLeft + index * xStep
            val y = h - paddingBottom - ((point.weight - minWeight) / weightRange * (h - paddingBottom))
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = Color(0xFF1B5E20),
            style = Stroke(width = 4f)
        )
    }
}