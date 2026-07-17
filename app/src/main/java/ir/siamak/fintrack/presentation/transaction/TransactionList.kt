package ir.siamak.fintrack.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    transactions: List<Transaction>,
    membersById: Map<Long, Member>,
    walletsById: Map<Long, Wallet>,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val groupedTransactions = transactions.groupBy { transaction ->
        formatDateHeader(transaction.date)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedTransactions.forEach { (date, itemsForDate) ->
            item(key = "header_$date") {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(
                items = itemsForDate,
                key = { it.id }
            ) { transaction ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onEditTransaction(transaction.id)
                                false
                            }

                            SwipeToDismissBoxValue.EndToStart -> {
                                onDeleteTransaction(transaction)
                                false
                            }

                            SwipeToDismissBoxValue.Settled -> false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        TransactionSwipeBackground(
                            dismissValue = dismissState.targetValue
                        )
                    }
                ) {
                    TransactionItemCard(
                        transaction = transaction,
                        memberName = membersById[transaction.memberId]?.name ?: "عضو نامشخص",
                        walletName = walletsById[transaction.walletId]?.name ?: "حساب نامشخص",
                        onClick = { onEditTransaction(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionSwipeBackground(
    dismissValue: SwipeToDismissBoxValue
) {
    val text = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> "ویرایش"
        SwipeToDismissBoxValue.EndToStart -> "حذف"
        SwipeToDismissBoxValue.Settled -> ""
    }

    val alignment = when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: Transaction,
    memberName: String,
    walletName: String,
    onClick: () -> Unit
) {
    val tagsText = transaction.tags
        .map { it.name }
        .filter { it.isNotBlank() }
        .joinToString("، ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = transaction.note.takeIf { it.isNotBlank() } ?: transaction.categoryName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = formatAmount(transaction.amount),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "عضو: $memberName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "حساب: $walletName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (tagsText.isNotBlank()) {
                Text(
                    text = "تگ‌ها: $tagsText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatTransactionDate(transaction.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    val rounded = amount.roundToLong()
    return "${formatter.format(rounded).toPersianDigits()} تومان"
}

private fun String.toPersianDigits(): String {
    val englishDigits = "0123456789"
    val persianDigits = "۰۱۲۳۴۵۶۷۸۹"

    return map { char ->
        val index = englishDigits.indexOf(char)
        if (index >= 0) persianDigits[index] else char
    }.joinToString("")
}

private fun timestampToPersianDate(timestamp: Long) = runCatching {
    val localDate = Instant
        .ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    JalaliDateConverter.fromGregorian(localDate)
}.getOrNull()

private fun formatDateHeader(timestamp: Long): String {
    val date = timestampToPersianDate(timestamp) ?: return "-"
    return PersianCalendarFormatter.formatFullDate(date)
}

private fun formatTransactionDate(timestamp: Long): String {
    val date = timestampToPersianDate(timestamp) ?: return "-"
    return PersianCalendarFormatter.formatDate(date)
}
