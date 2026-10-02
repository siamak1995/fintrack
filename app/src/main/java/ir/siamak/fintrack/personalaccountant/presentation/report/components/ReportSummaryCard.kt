package ir.siamak.fintrack.personalaccountant.presentation.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.components.SummaryCard
import ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.personalaccountant.presentation.theme.ErrorRed
import ir.siamak.fintrack.personalaccountant.presentation.theme.Success

@Composable
fun ReportsSummarySection(
    income: Double,
    expense: Double,
    saving: Double
) {

    SectionHeader(
        title = "خلاصه گزارش"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SummaryCard(
            title = "درآمد",
            amount = income,
            icon = Icons.Default.TrendingUp,
            iconBackground = Success,
            amountColor = Success
        )

        SummaryCard(
            title = "هزینه",
            amount = expense,
            icon = Icons.Default.TrendingDown,
            iconBackground = ErrorRed,
            amountColor = ErrorRed
        )

        SummaryCard(
            title = "پس‌انداز",
            amount = saving,
            icon = Icons.Default.Savings,
            iconBackground = MaterialTheme.colorScheme.primary,
            amountColor = MaterialTheme.colorScheme.primary
        )
    }
}
