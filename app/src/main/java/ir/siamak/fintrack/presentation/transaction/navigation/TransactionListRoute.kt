package ir.siamak.fintrack.presentation.transaction.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTButton
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.transaction.TransactionListViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionListRoute(
    onAddTransactionClick: () -> Unit,
    onEditTransactionClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var pendingDelete by remember { mutableStateOf<Transaction?>(null) }

    val groupedTransactions = remember(state.transactions) {
        state.transactions.groupBy { formatDateHeader(it.date) }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete?.let(viewModel::deleteTransaction)
                        pendingDelete = null
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("انصراف")
                }
            },
            title = { Text("حذف تراکنش") },
            text = { Text("آیا از حذف این تراکنش مطمئن هستید؟") }
        )
    }

    Scaffold(
        topBar = {
            FTTopBar(title = "تراکنش‌ها")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FTButton(
                text = "ثبت تراکنش جدید",
                onClick = onAddTransactionClick,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.transactions.isEmpty()) {
                Text(
                    text = "هنوز تراکنشی ثبت نشده است",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedTransactions.forEach { (dateHeader, transactions) ->
                        item(key = "header_$dateHeader") {
                            DateHeader(title = dateHeader)
                        }

                        items(
                            items = transactions,
                            key = { it.id }
                        ) { transaction ->
                            SwipeableTransactionItem(
                                transaction = transaction,
                                onEditClick = { onEditTransactionClick(transaction.id) },
                                onDeleteClick = { pendingDelete = transaction }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTransactionItem(
    transaction: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // در RTL:
    // کشیدن به راست = EndToStart از نظر Compose
    // کشیدن به چپ = StartToEnd از نظر Compose
    val editAction = if (isRtl) {
        SwipeToDismissBoxValue.StartToEnd
    } else {
        SwipeToDismissBoxValue.EndToStart
    }

    val deleteAction = if (isRtl) {
        SwipeToDismissBoxValue.EndToStart
    } else {
        SwipeToDismissBoxValue.StartToEnd
    }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.3f },
        confirmValueChange = { value ->
            when {
                value == editAction -> {
                    onEditClick()
                    false
                }
                value == deleteAction -> {
                    onDeleteClick()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val isEditDirection = if (isRtl) {
                direction == SwipeToDismissBoxValue.EndToStart
            } else {
                direction == SwipeToDismissBoxValue.StartToEnd
            }

            val isDeleteDirection = if (isRtl) {
                direction == SwipeToDismissBoxValue.StartToEnd
            } else {
                direction == SwipeToDismissBoxValue.EndToStart
            }

            val backgroundColor = when {
                isEditDirection -> MaterialTheme.colorScheme.primaryContainer
                isDeleteDirection -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }

            val alignment = when {
                isEditDirection -> Alignment.CenterStart
                isDeleteDirection -> Alignment.CenterEnd
                else -> Alignment.Center
            }

            val icon = when {
                isEditDirection -> Icons.Outlined.Edit
                isDeleteDirection -> Icons.Outlined.DeleteOutline
                else -> null
            }

            val tint = when {
                isEditDirection -> MaterialTheme.colorScheme.onPrimaryContainer
                isDeleteDirection -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
        }
    ) {
        TransactionListItem(
            transaction = transaction,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}




@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionListItem(
    transaction: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val (typeLabel, typeColor, typeIcon, amountPrefix) = when (transaction.type) {
        TransactionType.EXPENSE -> {
            Quadruple(
                "هزینه",
                MaterialTheme.colorScheme.error,
                Icons.Outlined.ArrowUpward,
                "-"
            )
        }
        TransactionType.INCOME -> {
            Quadruple(
                "درآمد",
                MaterialTheme.colorScheme.primary,
                Icons.Outlined.ArrowDownward,
                "+"
            )
        }
        TransactionType.TRANSFER -> {
            Quadruple(
                "انتقال",
                MaterialTheme.colorScheme.tertiary,
                Icons.Outlined.SyncAlt,
                ""
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = typeColor.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = typeLabel,
                            tint = typeColor,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = transaction.categoryName.ifBlank { "سایر" },
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = typeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = typeColor
                        )
                    }
                }

                Text(
                    text = if (amountPrefix.isBlank()) {
                        formatAmountForFa(transaction.amount)
                    } else {
                        "$amountPrefix ${formatAmountForFa(transaction.amount)}"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = typeColor
                )
            }

            if (transaction.note.isNotBlank()) {
                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (transaction.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    transaction.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            enabled = false,
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onEditClick) {
//                    Icon(
//                        imageVector = Icons.Outlined.Edit,
//                        contentDescription = "ویرایش"
//                    )
//                }
//
//                IconButton(onClick = onDeleteClick) {
//                    Icon(
//                        imageVector = Icons.Outlined.DeleteOutline,
//                        contentDescription = "حذف",
//                        tint = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
        }
    }
}

private fun formatDateHeader(timestamp: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale("fa"))
    return formatter.format(Date(timestamp)).toPersianDigits()
}

private fun formatAmountForFa(amount: Double): String {
    val normalized = BigDecimal.valueOf(amount).stripTrailingZeros()
    val pattern = if (normalized.scale() <= 0) "#,###" else "#,###.##"
    val formatter = DecimalFormat(pattern)
    return formatter.format(normalized).toPersianDigits()
}

private fun String.toPersianDigits(): String {
    return buildString(length) {
        for (char in this@toPersianDigits) {
            append(
                when (char) {
                    '0' -> '۰'
                    '1' -> '۱'
                    '2' -> '۲'
                    '3' -> '۳'
                    '4' -> '۴'
                    '5' -> '۵'
                    '6' -> '۶'
                    '7' -> '۷'
                    '8' -> '۸'
                    '9' -> '۹'
                    else -> char
                }
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
