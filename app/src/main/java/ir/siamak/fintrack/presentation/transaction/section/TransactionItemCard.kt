package ir.siamak.fintrack.presentation.transaction.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.core.extensions.timestampToPersianDate
import ir.siamak.fintrack.core.extensions.toPersianDigits
import ir.siamak.fintrack.data.model.Transaction
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionItemCard(
    transaction: Transaction,
    memberName: String,
    walletName: String,
    onClick: () -> Unit
) {
    val title = transaction.note.takeIf { it.isNotBlank() } ?: transaction.categoryName
    val tagsText = transaction.tags
        .map { it.name }
        .filter { it.isNotBlank() }

    val typeName = transaction.type.name

    val typeLabel = when (typeName) {
        "EXPENSE" -> "هزینه"
        "INCOME" -> "درآمد"
        "TRANSFER" -> "انتقال"
        else -> "تراکنش"
    }

    val amountColor = when (typeName) {
        "EXPENSE" -> MaterialTheme.colorScheme.error
        "INCOME" -> Color(0xFF2E7D32)
        "TRANSFER" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    val badgeContainerColor = when (typeName) {
        "EXPENSE" -> MaterialTheme.colorScheme.errorContainer
        "INCOME" -> MaterialTheme.colorScheme.primaryContainer
        "TRANSFER" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val badgeContentColor = when (typeName) {
        "EXPENSE" -> MaterialTheme.colorScheme.onErrorContainer
        "INCOME" -> MaterialTheme.colorScheme.onPrimaryContainer
        "TRANSFER" -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${formatTransactionDate(transaction.date)}  •  $memberName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatAmount(transaction.amount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = amountColor
                    )

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = badgeContainerColor
                    ) {
                        Text(
                            text = typeLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = badgeContentColor
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Text(
                    text = "حساب: $walletName",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (tagsText.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    tagsText.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.70f)
                        ) {
                            Text(
                                text = "#$tag",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTransactionDate(timestamp: Long): String {
    val date = timestampToPersianDate(timestamp) ?: return "-"
    return PersianCalendarFormatter.formatDate(date)
}

private fun formatAmount(amount: Double): String {
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    val rounded = amount.roundToLong()
    return "${formatter.format(rounded).toPersianDigits()} تومان"
}
