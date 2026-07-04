package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.core.datepicker.mapper.PersianDayCellUiModel
import ir.siamak.fintrack.core.datepicker.model.PersianDate

/**
 * Renders a single day cell in the Persian calendar grid.
 */
@Composable
fun PersianDayCell(
    item: PersianDayCellUiModel,
    onClick: (PersianDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        item.isSelected -> MaterialTheme.colorScheme.primary
        item.isInRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        else -> Color.Transparent
    }

    val contentColor = when {
        item.isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        item.isSelected -> MaterialTheme.colorScheme.onPrimary
        item.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(enabled = !item.isDisabled) { onClick(item.date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.date.day.toString(),
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (item.isToday || item.isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
