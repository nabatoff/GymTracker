package com.example.gymtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    points: List<Pair<Long, Float>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (points.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Недостаточно данных для графика")
        }
        return
    }

    val sortedPoints = remember(points) { points.sortedBy { it.first } }
    val minValue = sortedPoints.minOf { it.second }
    val maxValue = sortedPoints.maxOf { it.second }
    val range = if (maxValue - minValue == 0f) 1f else maxValue - minValue

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val spacing = 32f
        val usableWidth = chartWidth - spacing * 2
        val usableHeight = chartHeight - spacing * 2

        val path = Path()
        val pointsOffset = sortedPoints.mapIndexed { index, point ->
            val x = spacing + (index / (sortedPoints.size - 1f)) * usableWidth
            val normalizedY = (point.second - minValue) / range
            val y = chartHeight - spacing - normalizedY * usableHeight
            Offset(x, y)
        }

        path.moveTo(pointsOffset.first().x, pointsOffset.first().y)
        for (offset in pointsOffset.drop(1)) {
            path.lineTo(offset.x, offset.y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 6f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
