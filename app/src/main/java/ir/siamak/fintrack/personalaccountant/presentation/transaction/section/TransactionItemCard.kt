package ir.siamak.fintrack.personalaccountant.presentation.transaction.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.data.model.toPersian
import ir.siamak.fintrack.store.presentation.components.quantity

@Suppress("UNUSED_PARAMETER")
@Composable fun TransactionItemCard(transaction: Transaction, memberName: String, walletName: String, onClick: () -> Unit) {
    val title = transactionTitle(transaction)
    val color = when(transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
    }
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title,modifier = Modifier.weight(1f),style = MaterialTheme.typography.titleMedium,maxLines = 1,overflow = TextOverflow.Ellipsis)
                Text(quantity(transaction.amount) + " تومان",color = color,style = MaterialTheme.typography.titleMedium)
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(transaction.tags.joinToString("، ") { "#" + it.name }.ifBlank { "بدون تگ" },modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,maxLines = 1,overflow = TextOverflow.Ellipsis)
                Text(transaction.type.toPersian(),color = color,style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

internal fun transactionTitle(transaction: Transaction): String =
    if(transaction.note.startsWith("[FinTrackSms:") || transaction.note.startsWith("[FinTrackStore:")) transaction.categoryName
    else transaction.note.lineSequence().firstOrNull { it.isNotBlank() } ?: transaction.categoryName
