package ir.siamak.fintrack.domain.dashboard.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * یک آیتم نمایشی برای نمایش خلاصه یک تراکنش در داشبورد.
 *
 * این کامپوننت در لیست «آخرین تراکنش‌ها» استفاده می‌شود و اطلاعات زیر را نمایش می‌دهد:
 *
 * - آیکون متناسب با نوع تراکنش
 * - رنگ متناسب با نوع تراکنش
 * - عنوان تراکنش
 * - توضیحات تراکنش
 * - تاریخ تراکنش
 * - مبلغ تراکنش
 * - تگ‌های متصل به تراکنش
 *
 * منطق نمایش عنوان:
 * 1. اگر تراکنش تگ داشته باشد، عنوان از روی نام تگ‌ها ساخته می‌شود.
 * 2. اگر تگی وجود نداشته باشد، از `categoryName` استفاده می‌شود.
 * 3. اگر `categoryName` هم خالی باشد، عنوان پیش‌فرض بر اساس نوع تراکنش نمایش داده می‌شود.
 *
 * نکته مهم:
 * برای اینکه تگ‌ها واقعاً در اینجا نمایش داده شوند، فیلد `transaction.tags`
 * باید از لایه داده به‌درستی پر شده باشد. اگر این لیست خالی باشد، این کامپوننت
 * نمی‌تواند خودش تگ‌ها را از دیتابیس بخواند.
 *
 * @param transaction مدل تراکنش برای نمایش.
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

    val amount = if (transaction.type == TransactionType.EXPENSE) {
        -transaction.amount
    } else {
        transaction.amount
    }

    val date = SimpleDateFormat(
        "yyyy/MM/dd",
        Locale.getDefault()
    ).format(Date(transaction.date))

    FTCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transactionTitle(transaction),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = transaction.note.orEmpty().ifBlank { "بدون توضیح" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                TransactionTagsSection(transaction = transaction)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            MoneyText(
                amount = amount,
                showSign = true,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * عنوان مناسب تراکنش را تولید می‌کند.
 *
 * اولویت ساخت عنوان:
 * - اگر تراکنش دارای تگ باشد: نام تگ‌ها
 * - اگر تگ نداشت ولی categoryName داشت: categoryName
 * - اگر هیچ‌کدام نبود: عنوان پیش‌فرض بر اساس نوع تراکنش
 *
 * @param transaction تراکنش موردنظر
 * @return متن مناسب برای عنوان آیتم
 */
private fun transactionTitle(
    transaction: Transaction
): String {
    val tagsTitle = transaction.tags
        .map { it.name.trim() }
        .filter { it.isNotBlank() }
        .takeIf { it.isNotEmpty() }
        ?.joinToString("، ")

    if (!tagsTitle.isNullOrBlank()) return tagsTitle

    if (transaction.categoryName.isNotBlank()) {
        return transaction.categoryName
    }

    return when (transaction.type) {
        TransactionType.INCOME -> "درآمد"
        TransactionType.EXPENSE -> "هزینه"
        TransactionType.TRANSFER -> "انتقال"
    }
}

/**
 * بخش نمایش تگ‌های تراکنش.
 *
 * اگر تراکنش تگ داشته باشد، آن‌ها را به صورت چیپ نمایش می‌دهد.
 * اگر تگی وجود نداشته باشد، چیزی نمایش داده نمی‌شود.
 *
 * دلیل اینکه در حالت بدون تگ چیزی نمایش نمی‌دهیم این است که UI تمیزتر بماند
 * و متن تکراری مثل «بدون تگ» در همه آیتم‌ها شلوغی بصری ایجاد نکند.
 *
 * @param transaction تراکنشی که باید تگ‌های آن نمایش داده شود
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransactionTagsSection(
    transaction: Transaction
) {
    if (transaction.tags.isEmpty()) return

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        transaction.tags.take(3).forEach { tag ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tag.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }

        if (transaction.tags.size > 3) {
            Text(
                text = "+${transaction.tags.size - 3}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
