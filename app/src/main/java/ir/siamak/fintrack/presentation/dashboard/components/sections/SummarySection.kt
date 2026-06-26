package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.dashboard.components.items.FinanceInfoItem
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

/**
 * بخش خلاصه وضعیت مالی داشبورد.
 *
 * @param totalBalance مجموع موجودی کیف پول‌ها
 * @param income مجموع درآمد
 * @param expense مجموع هزینه
 */
@Composable
fun SummarySection(
    totalBalance: Double,
    income: Double,
    expense: Double
) {
    FTCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "موجودی کل",
                style = MaterialTheme.typography.labelMedium
            )

            MoneyText(
                amount = totalBalance,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceInfoItem("درآمد", income, Success)
                FinanceInfoItem("هزینه", expense, ErrorRed)
            }
        }
    }
}
