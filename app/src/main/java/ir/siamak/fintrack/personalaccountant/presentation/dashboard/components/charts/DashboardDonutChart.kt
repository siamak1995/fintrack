package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DashboardDonutChart(
    spending: Float,
    saving: Float
) {
    val animation = remember { Animatable(0f) }

    LaunchedEffect(spending, saving) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(900)
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(210.dp)
        ) {
            val stroke = 30f

            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFFEF4444),
                startAngle = -90f,
                sweepAngle = spending * 3.6f * animation.value,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFF22C55E),
                startAngle = -90f + (spending * 3.6f * animation.value),
                sweepAngle = saving * 3.6f * animation.value,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = "${saving.toInt()}%",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

