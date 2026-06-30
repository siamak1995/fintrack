package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.TransactionItem

/**
 * بخش نمایش تراکنش‌های اخیر.
 */
@Composable
fun TransactionSection(
    transactions: List<Transaction>
) {
    Column {
        SectionHeader(title = "تراکنش‌های اخیر")

        if (transactions.isEmpty()) {
            EmptySectionText("تراکنشی ثبت نشده است.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.forEach { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}
