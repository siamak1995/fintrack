package ir.siamak.fintrack.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.presentation.graphics.toArgb
import androidx.compose.presentation.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    error = ErrorRed,
    background = BackgroundGray,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

// دسترسی راحت به سیستم‌های طراحی
object AppTheme {
    val spacing: Spacing
        @Composable @ReadOnlyComposable get() = LocalSpacing.current
    val dimens: Dimens
        @Composable @ReadOnlyComposable get() = LocalDimens.current
}

@Composable
fun FinTrackTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // تزریق تمام سیستم‌ها به اپلیکیشن
    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalDimens provides Dimens()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes, // اعمال Shapes
            content = content
        )
    }
}
