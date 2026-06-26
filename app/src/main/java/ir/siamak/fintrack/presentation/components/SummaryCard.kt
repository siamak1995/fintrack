package ir.siamak.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.theme.AppTheme

/**
 * کارت خلاصه مالی برای نمایش یک شاخص پولی مهم.
 *
 * این کامپوننت معمولاً برای نمایش مقادیر مالی مثل:
 * - موجودی کل
 * - درآمد ماه
 * - هزینه ماه
 * - مانده حساب
 *
 * استفاده می‌شود.
 *
 * @param title عنوان کارت
 * @param amount مبلغ قابل نمایش
 * @param icon آیکون مرتبط با شاخص
 * @param iconBackground رنگ اصلی آیکون و پس‌زمینه آن
 * @param amountColor رنگ متن مبلغ
 * @param modifier استایل‌های اضافی
 */
@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    iconBackground: Color,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    FTCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconBackground.copy(alpha = 0.14f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBackground
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            MoneyText(
                amount = amount,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor
            )
        }
    }
}
