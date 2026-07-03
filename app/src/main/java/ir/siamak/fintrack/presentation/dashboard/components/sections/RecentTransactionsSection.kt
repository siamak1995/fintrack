package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.dashboard.items.TransactionItem
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    onShowAll: (() -> Unit)? = null
) {

    Column {

        SectionHeader("آخرین تراکنش‌ها")

        if (transactions.isEmpty()) {
            EmptySectionText("هنوز تراکنشی ثبت نشده است.")
            return
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            transactions.take(5).forEach { transaction ->

                TransactionItem(transaction)

            }
        }

        if (transactions.size > 5 && onShowAll != null) {

            TextButton(onClick = onShowAll) {
                Text("مشاهده همه")
            }
        }
    }
}