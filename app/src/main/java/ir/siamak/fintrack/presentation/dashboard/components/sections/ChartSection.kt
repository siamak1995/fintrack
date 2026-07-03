package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard

@Composable
fun ChartSection(
    spending: Float,
    saving: Float
) {

    FTCard {

        Column(Modifier.padding(16.dp)) {

            Text("نمودار مالی")

            Spacer(Modifier.height(12.dp))

            Text("Spending: $spending %")
            Text("Saving: $saving %")
        }
    }
}