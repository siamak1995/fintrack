package ir.siamak.fintrack.common.core.datepicker.preview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.datepicker.mapper.PersianCalendarUiMapper
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate
import ir.siamak.fintrack.common.core.datepicker.model.PersianDateRange
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth

@Composable
private fun PreviewDayCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PersianCalendarUiMapperPreview() {
    val displayedMonth = PersianMonth(year = 1405, month = 4)

    val mockDays = buildList {
        addAll((29..31).map { PersianDate(1405, 3, it) })
        addAll((1..31).map { PersianDate(1405, 4, it) })
        addAll((1..8).map { PersianDate(1405, 5, it) })
    }

    val uiModels = PersianCalendarUiMapper.mapMonthDays(
        displayedMonth = displayedMonth,
        days = mockDays,
        selectedRange = PersianDateRange(
            start = PersianDate(1405, 4, 10),
            end = PersianDate(1405, 4, 16)
        ),
        today = PersianDate(1405, 4, 12)
    )

    Surface {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(uiModels) { item ->
                val label = buildString {
                    append(item.date.day)
                    if (item.isToday) append(" • Today")
                    if (item.isSelected) append(" • Selected")
                    if (item.isInRange) append(" • InRange")
                    if (!item.isCurrentMonth) append(" • OtherMonth")
                }

                PreviewDayCell(text = label)
            }
        }
    }
}

