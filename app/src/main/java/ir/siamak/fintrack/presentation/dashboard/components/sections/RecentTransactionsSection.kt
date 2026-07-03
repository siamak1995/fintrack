package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.TransactionItem

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>
) {

    Column {

        SectionHeader("تراکنش‌های اخیر")

        LazyColumn(
            userScrollEnabled = false
        ) {

            items(transactions) { TransactionItem(it) }
        }
    }
}