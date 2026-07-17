package ir.siamak.fintrack.presentation.dashboard.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

    val animation = remember {

        Animatable(0f)

    }

    LaunchedEffect(spending, saving) {

        animation.animateTo(

            1f,

            tween(900)

        )

    }

    Box(

        contentAlignment = Alignment.Center,

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Canvas(

            modifier = Modifier.size(210.dp)

        ) {

            val stroke = 30f

            drawArc(

                color = Color(0xffE5E7EB),

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

                color = Color(0xffEF4444),

                startAngle = -90f,

                sweepAngle =

                    spending * 3.6f * animation.value,

                useCenter = false,

                topLeft = Offset.Zero,

                size = Size(size.width, size.height),

                style = Stroke(

                    width = stroke,

                    cap = StrokeCap.Round

                )

            )

            drawArc(

                color = Color(0xff22C55E),

                startAngle =

                    -90f + spending * 3.6f * animation.value,

                sweepAngle =

                    saving * 3.6f * animation.value,

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

            "${saving.toInt()}%",

            style = MaterialTheme.typography.headlineMedium

        )

    }

}