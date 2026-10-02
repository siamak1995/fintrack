package ir.siamak.fintrack.personalaccountant.presentation.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.common.core.extensions.PersianMonthKey
import ir.siamak.fintrack.common.core.extensions.timestampToPersianDate
import ir.siamak.fintrack.common.core.extensions.toPersianDigits
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionDateGlassHeader
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionItemCard
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionMonthGlassHeader
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionSwipeBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionList(
    transactions: List<Transaction>,
    membersById: Map<Long, Member>,
    walletsById: Map<Long, Wallet>,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val expandedMonths = remember {
        mutableStateMapOf<PersianMonthKey, Boolean>()
    }

    val expandedDates = remember {
        mutableStateMapOf<String, Boolean>()
    }

    val groupedTransactionsByMonth = remember(transactions) {
        transactions
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
                keySelector = PersianMonthTransaction::monthKey,
                valueTransform = PersianMonthTransaction::transaction
            )
            .toList()
            .sortedByDescending { (monthKey, _) ->
                monthKey.sortValue
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        groupedTransactionsByMonth.forEachIndexed { monthIndex, monthGroup ->
            val monthKey = monthGroup.first
            val monthTransactions = monthGroup.second

            item(
                key = "month_${monthKey.key}"
            ) {
                val isMonthExpanded =
                    expandedMonths[monthKey] ?: (monthIndex == 0)

                TransactionMonthContainer {
                    TransactionMonthGlassHeader(
                        monthKey = monthKey,
                        count = monthTransactions.size,
                        isExpanded = isMonthExpanded,
                        onClick = {
                            expandedMonths[monthKey] = !isMonthExpanded
                        }
                    )

                    AnimatedVisibility(
                        visible = isMonthExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        MonthTransactionsContent(
                            monthKey = monthKey,
                            transactions = monthTransactions,
                            expandedDates = expandedDates,
                            membersById = membersById,
                            walletsById = walletsById,
                            onEditTransaction = onEditTransaction,
                            onDeleteTransaction = onDeleteTransaction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionMonthContainer(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthTransactionsContent(
    monthKey: PersianMonthKey,
    transactions: List<Transaction>,
    expandedDates: MutableMap<String, Boolean>,
    membersById: Map<Long, Member>,
    walletsById: Map<Long, Wallet>,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val groupedTransactionsByDate = remember(transactions) {
        transactions
            .groupBy { transaction ->
                formatDateHeader(transaction.date)
            }
            .toList()
            .sortedByDescending { (_, transactionsForDate) ->
                transactionsForDate.maxOfOrNull(Transaction::date) ?: 0L
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedTransactionsByDate.forEachIndexed { dateIndex, dateGroup ->
            val date = dateGroup.first
            val transactionsForDate = dateGroup.second
            val dateStateKey = "${monthKey.key}_$date"
            val isDateExpanded = expandedDates[dateStateKey] ?: false

            key(dateStateKey) {
                if (dateIndex > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(
                            alpha = 0.65f
                        )
                    )
                }

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
                            top = 6.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        transactionsForDate
                            .sortedByDescending(Transaction::date)
                            .forEach { transaction ->
                                key(transaction.id) {
                                    SwipeableTransactionItem(
                                        transaction = transaction,
                                        memberName = membersById[
                                            transaction.memberId
                                        ]?.name ?: "عضو نامشخص",
                                        walletName = walletsById[
                                            transaction.walletId
                                        ]?.name ?: "حساب نامشخص",
                                        onEditTransaction = onEditTransaction,
                                        onDeleteTransaction = onDeleteTransaction
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTransactionItem(
    transaction: Transaction,
    memberName: String,
    walletName: String,
    onEditTransaction: (Long) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit
) {
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
            memberName = memberName,
            walletName = walletName,
            onClick = {
                onEditTransaction(transaction.id)
            }
        )
    }
}

private fun formatDateHeader(timestamp: Long): String {
    val date = timestampToPersianDate(timestamp) ?: return "-"

    return PersianCalendarFormatter
        .formatFullDate(date)
        .toPersianDigits()
}

private data class PersianMonthTransaction(
    val monthKey: PersianMonthKey,
    val transaction: Transaction
)

