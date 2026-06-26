package ir.siamak.fintrack.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val screenHorizontalPadding: Dp = 16.dp,
    val screenVerticalPadding: Dp = 16.dp,
    val cardElevation: Dp = 2.dp,
    val buttonHeight: Dp = 52.dp,
    val textFieldHeight: Dp = 56.dp,
    val topBarHeight: Dp = 64.dp,
    val bottomBarHeight: Dp = 72.dp,
    val iconSmall: Dp = 18.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
