package ir.siamak.fintrack.presentation.dashboard.components.items

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ir.siamak.fintrack.presentation.components.MoneyText


/**
 * آیتم نمایش مقدار مالی خلاصه.
 */
@Composable
fun FinanceInfoItem(
    label: String,
    amount: Double,
    color: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
        MoneyText(
            amount = amount,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
