package ir.siamak.fintrack.presentation.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.SummaryCard
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success
import androidx.compose.foundation.layout.Row

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
            color = Success
        )

        SummaryCard(
            title = "هزینه",
            amount = expense,
            color = ErrorRed
        )

        SummaryCard(
            title = "پس‌انداز",
            amount = saving,
            color = MaterialTheme.colorScheme.primary
        )
    }
}