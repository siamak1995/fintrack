package ir.siamak.fintrack.personalaccountant.presentation.security.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val keypadRows = listOf(
    listOf(1, 2, 3),
    listOf(4, 5, 6),
    listOf(7, 8, 9)
)

/**
 * Numeric keypad used for entering the application PIN.
 */
@Composable
fun PinPad(
    isEnabled: Boolean,
    onDigitClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keypadRows.forEach { digits ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                digits.forEach { digit ->
                    PinDigitButton(
                        digit = digit,
                        enabled = isEnabled,
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeypadPlaceholder()

            PinDigitButton(
                digit = 0,
                enabled = isEnabled,
                onClick = { onDigitClick(0) }
            )

            BackspaceButton(
                enabled = isEnabled,
                onClick = onBackspaceClick
            )
        }
    }
}

@Composable
private fun PinDigitButton(
    digit: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(KEY_SIZE)
    ) {
        Text(
            text = digit.toPersianDigit(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            }
        )
    }
}

@Composable
private fun BackspaceButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(KEY_SIZE)
    ) {
        /*
         * Prevents the surrounding RTL layout from mirroring the backspace icon.
         */
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr
        ) {
            Icon(
                imageVector = Icons.Rounded.Backspace,
                contentDescription = "پاک کردن",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun KeypadPlaceholder() {
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.size(KEY_SIZE)
    )
}

private fun Int.toPersianDigit(): String {
    return when (this) {
        0 -> "۰"
        1 -> "۱"
        2 -> "۲"
        3 -> "۳"
        4 -> "۴"
        5 -> "۵"
        6 -> "۶"
        7 -> "۷"
        8 -> "۸"
        9 -> "۹"
        else -> toString()
    }
}

private val KEY_SIZE = 64.dp

