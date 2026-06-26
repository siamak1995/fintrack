package ir.siamak.fintrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.theme.AppTheme

/**
 * یک کارت ساده برای نمایش آمارهای عددی یا متنی (بدون فرمت مالی).
 *
 * @param title عنوان آمار (مثلاً "تعداد کیف پول")
 * @param value مقدار نمایش داده شده (به صورت رشته)
 * @param icon آیکون بخش
 * @param iconBackground رنگ پس‌زمینه آیکون
 * @param modifier استایل‌های اضافی
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBackground: Color,
    modifier: Modifier = Modifier
) {
    FTCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(iconBackground.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(AppTheme.spacing.small))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
