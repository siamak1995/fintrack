package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.components.SummaryCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinancialSummarySection(
    totalBalance: Double,
    walletBalance: Double,
    income: Double,
    expense: Double
) {

    Column {

        SectionHeader(title = "خلاصه مالی")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SummaryCard("موجودی کیف ها", totalBalance, MaterialTheme.colorScheme.primary)
            SummaryCard("مجموع دارائی", walletBalance, Color(0xFF7C3AED))
            SummaryCard("جمع درآمد ها", income, Success)
            SummaryCard("جمع هزینه ها", expense, ErrorRed)
        }
    }
}

@Composable
private fun SummaryMiniItem(
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            color.copy(.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )

        }

        Text(
            text = title,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.labelMedium
        )

        MoneyText(
            amount = amount,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )

    }

}