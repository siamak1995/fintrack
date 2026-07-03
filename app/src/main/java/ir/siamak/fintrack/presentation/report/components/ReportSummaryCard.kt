package ir.siamak.fintrack.presentation.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText

@Composable
fun ReportSummaryCard(

    modifier: Modifier = Modifier,

    title: String,

    amount: Double,

    color: Color

) {

    FTCard(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            MoneyText(
                amount = amount,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}