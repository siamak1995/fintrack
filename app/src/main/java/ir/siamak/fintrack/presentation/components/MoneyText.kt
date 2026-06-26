package ir.siamak.fintrack.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import ir.siamak.fintrack.presentation.theme.Success

@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    currency: String = "تومان",
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = Success,
    showSign: Boolean = false
) {
    val formatted = "%,.0f".format(amount)
    val sign = when {
        !showSign -> ""
        amount > 0 -> "+"
        amount < 0 -> "-"
        else -> ""
    }

    Text(
        text = "$sign$formatted $currency",
        modifier = modifier,
        style = style,
        color = color
    )
}
