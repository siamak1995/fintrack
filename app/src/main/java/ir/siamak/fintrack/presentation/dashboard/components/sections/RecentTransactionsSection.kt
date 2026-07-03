package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.SectionHeader
import ir.siamak.fintrack.domain.dashboard.items.TransactionItem

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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {

            items(
                items = transactions.take(5),
                key = { it.id }
            ) {

                TransactionItem(it)

            }

        }

        if (transactions.size > 5 && onShowAll != null) {

            TextButton(
                modifier = Modifier.padding(top = 8.dp),
                onClick = onShowAll
            ) {

                Text("مشاهده همه")

            }

        }

    }

}