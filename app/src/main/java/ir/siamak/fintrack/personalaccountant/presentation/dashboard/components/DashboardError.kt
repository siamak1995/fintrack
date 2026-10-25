package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components

import ir.siamak.fintrack.common.core.formatter.PersianText as Text
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
