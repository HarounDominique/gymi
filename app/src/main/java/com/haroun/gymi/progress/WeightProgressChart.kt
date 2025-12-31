package com.haroun.gymi.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.haroun.gymi.domain.model.ProgressPoint

@Composable
fun WeightProgressChart(
    points: List<ProgressPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxWeight = points.maxOf { it.weight }
    val minWeight = points.minOf { it.weight }

    Canvas(
        modifier = modifier
            .height(220.dp)
            .padding(16.dp)
    ) {
        val spacing = 40f
        val chartWidth = size.width - spacing
        val chartHeight = size.height - spacing

        val stepX = chartWidth / (points.size - 1).coerceAtLeast(1)

        fun weightToY(weight: Float): Float {
            return chartHeight *
                    (1f - (weight - minWeight) / (maxWeight - minWeight + 0.01f))
        }

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = spacing + index * stepX
            val y = weightToY(point.weight)

            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        points.forEachIndexed { index, point ->
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 6f,
                center = Offset(
                    spacing + index * stepX,
                    weightToY(point.weight)
                )
            )
        }
    }
}
