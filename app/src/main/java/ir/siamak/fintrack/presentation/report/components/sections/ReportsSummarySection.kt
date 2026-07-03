package ir.siamak.fintrack.presentation.reports.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.reports.components.ReportSummaryCard
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

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

        ReportSummaryCard(

            modifier = Modifier.weight(1f),

            title = "درآمد",

            amount = income,

            color = Success

        )

        ReportSummaryCard(

            modifier = Modifier.weight(1f),

            title = "هزینه",

            amount = expense,

            color = ErrorRed

        )

        ReportSummaryCard(

            modifier = Modifier.weight(1f),

            title = "پس‌انداز",

            amount = saving,

            color = MaterialTheme.colorScheme.primary

        )

    }

}