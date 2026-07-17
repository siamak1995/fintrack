package ir.siamak.fintrack.presentation.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.settings.FirstDayOfWeek

@Composable
fun GeneralSection(
    firstDay: FirstDayOfWeek,
    onFirstDayChanged: (FirstDayOfWeek) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "عمومی",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "اولین روز هفته",
                style = MaterialTheme.typography.titleSmall
            )

            FirstDayOfWeek.entries.forEach { item ->
                SectionRadioRow(
                    title = item.name,
                    selected = firstDay == item,
                    onClick = { onFirstDayChanged(item) }
                )
            }
        }
    }
}
