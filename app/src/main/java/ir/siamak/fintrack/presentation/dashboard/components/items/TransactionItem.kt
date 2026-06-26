package ir.siamak.fintrack.presentation.dashboard.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success


/**
 * نمایش یک تراکنش تکی در لیست تراکنش‌ها.
 */
@Composable
fun TransactionItem(
    transaction: Transaction
) {
    val color = when (transaction.type) {
        TransactionType.INCOME -> Success
        TransactionType.EXPENSE -> ErrorRed
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.primary
    }

    val icon = when (transaction.type) {
        TransactionType.INCOME -> Icons.Default.CallReceived
        TransactionType.EXPENSE -> Icons.Default.CallMade
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }

    FTCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = transaction.note ?: "بدون توضیح",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            MoneyText(
                amount = transaction.amount,
                color = color,
                style = MaterialTheme.typography.titleSmall,
                showSign = true
            )
        }
    }
}
