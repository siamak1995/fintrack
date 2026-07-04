package ir.siamak.fintrack.core.datepicker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a compact list of selectable Persian years.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersianYearPicker(
    selectedYear: Int,
    years: List<Int>,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        years.forEach { year ->
            FilterChip(
                selected = year == selectedYear,
                onClick = { onYearSelected(year) },
                label = {
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}
