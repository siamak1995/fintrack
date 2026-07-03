package ir.siamak.fintrack.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    color: Color
) {

    FTCard(
        modifier = Modifier.width(160.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(title, style = MaterialTheme.typography.labelMedium)

            Spacer(Modifier.height(8.dp))

            MoneyText(
                amount = amount,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}