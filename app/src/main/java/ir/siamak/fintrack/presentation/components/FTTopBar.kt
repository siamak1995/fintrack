package ir.siamak.fintrack.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ir.siamak.fintrack.presentation.theme.AppTheme

/**
 * نوار بالای ساده و reusable برای صفحات اپلیکیشن.
 *
 * این کامپوننت برای نمایش عنوان صفحه و اکشن اختیاری سمت چپ/راست استفاده می‌شود
 * و می‌تواند در داشبورد، لیست حساب‌ها، تنظیمات و سایر صفحات مورد استفاده قرار گیرد.
 *
 * @param title عنوان اصلی صفحه
 * @param subtitle زیرعنوان اختیاری
 * @param actionIcon آیکون اکشن اختیاری
 * @param actionContentDescription توضیح accessibility برای آیکون
 * @param onActionClick callback اکشن
 */
@Composable
fun FTTopBar(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    actionIcon: ImageVector? = null,
    navigationIcon: ImageVector? = null,
    actionContentDescription: String? = null,
    onActionClick: (() -> Unit)? = null,
    onNavigationClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (actionIcon != null && onActionClick != null) {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionContentDescription
                )
            }
        }
    }
}
