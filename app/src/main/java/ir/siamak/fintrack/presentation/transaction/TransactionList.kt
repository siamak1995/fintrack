package ir.siamak.fintrack.presentation.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.core.extensions.PersianMonthKey
import ir.siamak.fintrack.core.extensions.timestampToPersianDate
import ir.siamak.fintrack.core.extensions.toPersianDigits
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.transaction.section.TransactionDateGlassHeader
import ir.siamak.fintrack.presentation.transaction.section.TransactionItemCard
import ir.siamak.fintrack.presentation.transaction.section.TransactionMonthGlassHeader
import ir.siamak.fintrack.presentation.transaction.section.TransactionSwipeBackground


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    transactions: List<Transaction>,
    membersById: Map<Long, Member>,
    walletsById: Map<Long, Wallet>,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val expandedMonths = remember { mutableStateMapOf<PersianMonthKey, Boolean>() }
    val expandedDates = remember { mutableStateMapOf<String, Boolean>() }

    val groupedTransactionsByMonth = transactions
        .mapNotNull { transaction ->
            val persianDate = timestampToPersianDate(transaction.date)
                ?: return@mapNotNull null

            PersianMonthTransaction(
                monthKey = PersianMonthKey(
                    year = persianDate.year,
                    month = persianDate.month
                ),
                transaction = transaction
            )
        }
        .groupBy(
            keySelector = { it.monthKey },
            valueTransform = { it.transaction }
        )
        .toList()
        .sortedByDescending { (monthKey, _) ->
            monthKey.sortValue
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedTransactionsByMonth.forEachIndexed { monthIndex, (monthKey, monthTransactions) ->
            val isMonthExpanded = expandedMonths[monthKey] ?: (monthIndex == 0)

            item(key = "month_header_${monthKey.key}") {
                TransactionMonthGlassHeader(
                    monthKey = monthKey,
                    count = monthTransactions.size,
                    isExpanded = isMonthExpanded,
                    onClick = {
                        expandedMonths[monthKey] = !isMonthExpanded
                    }
                )
            }

            item(key = "month_content_${monthKey.key}") {
                AnimatedVisibility(
                    visible = isMonthExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val groupedTransactionsByDate = monthTransactions
                            .groupBy { transaction ->
                                formatDateHeader(transaction.date)
                            }
                            .toList()
                            .sortedByDescending { (_, transactionsForDate) ->
                                transactionsForDate.maxOfOrNull { it.date } ?: 0L
                            }

                        groupedTransactionsByDate.forEach { (date, transactionsForDate) ->
                            val dateStateKey = "${monthKey.key}_$date"
                            val isDateExpanded = expandedDates[dateStateKey] ?: false

                            key(dateStateKey) {
                                TransactionDateGlassHeader(
                                    date = date,
                                    count = transactionsForDate.size,
                                    isExpanded = isDateExpanded,
                                    onClick = {
                                        expandedDates[dateStateKey] = !isDateExpanded
                                    }
                                )

                                AnimatedVisibility(
                                    visible = isDateExpanded,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            start = 8.dp,
                                            top = 2.dp
                                        ),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        transactionsForDate
                                            .sortedByDescending { it.date }
                                            .forEach { transaction ->
                                                val dismissState =
                                                    rememberSwipeToDismissBoxState(
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

                                                                SwipeToDismissBoxValue.Settled -> {
                                                                    false
                                                                }
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
                                                        memberName = membersById[
                                                            transaction.memberId
                                                        ]?.name ?: "عضو نامشخص",
                                                        walletName = walletsById[
                                                            transaction.walletId
                                                        ]?.name ?: "حساب نامشخص",
                                                        onClick = {
                                                            onEditTransaction(transaction.id)
                                                        }
                                                    )
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


private fun formatDateHeader(timestamp: Long): String {
    val date = timestampToPersianDate(timestamp) ?: return "-"
    return PersianCalendarFormatter.formatFullDate(date).toPersianDigits()
}


private data class PersianMonthTransaction(
    val monthKey: PersianMonthKey,
    val transaction: Transaction
)

