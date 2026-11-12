package com.crypto.forecast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.crypto.forecast.data.model.ForecastPoint
import com.crypto.forecast.ui.theme.*

@Composable
fun ForecastChart(
    points: List<ForecastPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = AccentCyan
) {
    if (points.isEmpty()) return

    val minPrice = points.minOf { it.lowerBand }
    val maxPrice = points.maxOf { it.upperBand }
    val priceDiff = (maxPrice - minPrice).coerceAtLeast(1.0)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Confidence Band (Shaded area)
            val bandPath = Path()
            points.forEachIndexed { index, point ->
                val x = (index.toFloat() / (points.size - 1)) * width
                val yUpper = height - ((point.upperBand - minPrice) / priceDiff).toFloat() * height
                if (index == 0) bandPath.moveTo(x, yUpper) else bandPath.lineTo(x, yUpper)
            }
            for (index in points.indices.reversed()) {
                val point = points[index]
                val x = (index.toFloat() / (points.size - 1)) * width
                val yLower = height - ((point.lowerBand - minPrice) / priceDiff).toFloat() * height
                bandPath.lineTo(x, yLower)
            }
            bandPath.close()

            drawPath(
                path = bandPath,
                color = lineColor.copy(alpha = 0.15f)
            )

            // 2. Draw Main Forecast Line
            val linePath = Path()
            points.forEachIndexed { index, point ->
                val x = (index.toFloat() / (points.size - 1)) * width
                val y = height - ((point.price - minPrice) / priceDiff).toFloat() * height
                if (index == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Draw End Point Dot
            val lastPoint = points.last()
            val endX = width
            val endY = height - ((lastPoint.price - minPrice) / priceDiff).toFloat() * height
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(endX, endY)
            )
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = Offset(endX, endY)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Min: $${String.format("%.2f", minPrice)}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Maks: $${String.format("%.2f", maxPrice)}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
