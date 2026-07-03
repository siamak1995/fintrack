package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

@Composable
fun ChartSection(

    spending: Float,

    saving: Float

) {

    FTCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "وضعیت این ماه",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "پس‌انداز",
                modifier = Modifier.padding(top = 16.dp)
            )

            LinearProgressIndicator(
                progress = { saving / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = Success
            )

            Text(
                text = "${saving.toInt()} %",
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "هزینه",
                modifier = Modifier.padding(top = 18.dp)
            )

            LinearProgressIndicator(
                progress = { spending / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = ErrorRed
            )

            Text(
                text = "${spending.toInt()} %",
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    "وضعیت",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    if (saving >= spending)
                        "عالی"
                    else
                        "نیاز به مدیریت",
                    color =
                        if (saving >= spending)
                            Success
                        else
                            ErrorRed
                )

            }

        }

    }

}