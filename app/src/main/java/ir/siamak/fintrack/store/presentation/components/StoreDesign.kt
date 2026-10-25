package ir.siamak.fintrack.store.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ir.siamak.fintrack.R

/** Store-only typography; keeps the user's existing light/dark palette and personal-accountant theme. */
@Composable
fun StoreDesign(enabled: Boolean, content: @Composable () -> Unit) {
    val base = MaterialTheme.typography
    val family = FontFamily(Font(R.font.store_regular, FontWeight.Normal), Font(R.font.store_bold, FontWeight.Bold))
    val typography = if(!enabled) base else base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = family), displayMedium = base.displayMedium.copy(fontFamily = family), displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family), headlineMedium = base.headlineMedium.copy(fontFamily = family), headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family), titleMedium = base.titleMedium.copy(fontFamily = family), titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family), bodyMedium = base.bodyMedium.copy(fontFamily = family), bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family), labelMedium = base.labelMedium.copy(fontFamily = family), labelSmall = base.labelSmall.copy(fontFamily = family))
    MaterialTheme(typography = typography,content = content)
}
