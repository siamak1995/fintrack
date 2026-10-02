package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DashboardError(
    message: String
) {

    Text(

        text = message,

        color = Color.Red

    )

}
