package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliMonthCalculator
import ir.siamak.fintrack.common.core.datepicker.calendar.PersianCalendarFormatter

/**
 * Displays the Persian week day labels in calendar order.
 */
@Composable
fun PersianWeekHeader(
    modifier: Modifier = Modifier
) {
    val weekDays = JalaliMonthCalculator.getWeekDaysOrdered()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEach { day ->
            Text(
                text = PersianCalendarFormatter.getShortWeekDayName(day),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

