package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.FinancialHealth
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.charts.ChartLegend
import ir.siamak.fintrack.presentation.dashboard.components.charts.DashboardDonutChart
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

@Composable
fun ChartSection(
    spending: Float,
    saving: Float,
    health: FinancialHealth
) {
    FTCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            SectionHeader(
                title = "وضعیت مالی این ماه",
                icon = Icons.Default.PieChart
            )

            Spacer(modifier = Modifier.height(20.dp))

            DashboardDonutChart(
                spending = spending,
                saving = saving
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartLegend(
                    title = "هزینه",
                    value = spending,
                    color = ErrorRed
                )

                ChartLegend(
                    title = "پس‌انداز",
                    value = saving,
                    color = Success
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "جمع‌بندی وضعیت",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            FinancialHealthCard(health = health)
        }
    }
}

@Composable
private fun FinancialHealthCard(
    health: FinancialHealth
) {
    val title: String
    val color: Color

    when (health) {
        FinancialHealth.EXCELLENT -> {
            title = "سلامت مالی عالی"
            color = Success
        }

        FinancialHealth.GOOD -> {
            title = "سلامت مالی خوب"
            color = Color(0xFF2563EB)
        }

        FinancialHealth.WARNING -> {
            title = "نیاز به مدیریت هزینه"
            color = Color(0xFFF59E0B)
        }

        FinancialHealth.DANGER -> {
            title = "وضعیت بحرانی"
            color = ErrorRed
        }
    }

    Text(
        text = title,
        color = color,
        style = MaterialTheme.typography.titleMedium
    )
}
