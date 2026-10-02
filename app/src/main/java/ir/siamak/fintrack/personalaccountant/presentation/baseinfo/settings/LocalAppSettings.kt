package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings

import androidx.compose.runtime.staticCompositionLocalOf
import ir.siamak.fintrack.personalaccountant.domain.settings.AppSettings

/**
 * CompositionLocal سراسری برای دسترسی UIها به تنظیمات برنامه.
 */
val LocalAppSettings = staticCompositionLocalOf {
    AppSettings()
}

