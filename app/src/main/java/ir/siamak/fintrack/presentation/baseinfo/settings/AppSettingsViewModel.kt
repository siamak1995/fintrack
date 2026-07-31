package ir.siamak.fintrack.presentation.baseinfo.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.domain.settings.ObserveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel سراسری تنظیمات برنامه.
 *
 * این ViewModel تنظیمات ذخیره‌شده را به‌صورت زنده در اختیار ریشه UI قرار می‌دهد.
 */
@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = observeSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )
}
