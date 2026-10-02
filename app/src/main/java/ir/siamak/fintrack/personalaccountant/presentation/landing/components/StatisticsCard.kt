package ir.siamak.fintrack.personalaccountant.presentation.landing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard

@Composable
fun StatisticsCard(
    title: String,
    value: Float
) {

    FTCard {

        Column(
            Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            LinearProgressIndicator(
                progress = { value },
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "${(value * 100).toInt()}%",
                modifier = Modifier.padding(top = 8.dp)
            )

        }

    }

}
