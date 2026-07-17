package ir.siamak.fintrack.presentation.dashboard.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChartLegend(

    title: String,

    value: Float,

    color: Color

) {

    Row(

        verticalAlignment = Alignment.CenterVertically

    ) {

        Box(

            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)

        )

        Spacer(Modifier.width(8.dp))

        Text(

            "$title (${value.toInt()}%)",

            style = MaterialTheme.typography.bodyMedium

        )

    }

}