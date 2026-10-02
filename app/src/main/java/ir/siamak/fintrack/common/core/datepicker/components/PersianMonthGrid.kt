package ir.siamak.fintrack.common.core.datepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.siamak.fintrack.common.core.datepicker.mapper.PersianDayCellUiModel
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate

/**
 * Displays a non-scrollable 7-column month grid.
 */
@Composable
fun PersianMonthGrid(
    items: List<PersianDayCellUiModel>,
    onDayClick: (PersianDate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.fillMaxWidth(),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(items) { item ->
            PersianDayCell(
                item = item,
                onClick = onDayClick
            )
        }
    }
}

