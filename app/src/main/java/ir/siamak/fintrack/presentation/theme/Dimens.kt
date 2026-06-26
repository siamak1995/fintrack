package ir.siamak.fintrack.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val borderWidth: Dp = 1.dp,
    val buttonHeight: Dp = 48.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
