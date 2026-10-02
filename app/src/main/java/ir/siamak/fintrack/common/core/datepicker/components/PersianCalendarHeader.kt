package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.calendar.PersianCalendarFormatter
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

@Composable
fun PersianCalendarHeader(
    month: PersianMonth,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNextMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "ماه بعد"
            )
        }

        Text(
            text = PersianCalendarFormatter.formatMonthYear(month),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onPreviousMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "ماه قبل"
            )
        }
    }
}

