package ir.siamak.fintrack.presentation.baseinfo.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.security.usecase.ClearPinUseCase
import ir.siamak.fintrack.domain.security.usecase.ObserveSecuritySettingsUseCase
import ir.siamak.fintrack.domain.security.usecase.SetBiometricEnabledUseCase
import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.domain.settings.GetSettingsUseCase
import ir.siamak.fintrack.domain.settings.ResetSettingsUseCase
import ir.siamak.fintrack.domain.settings.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel صفحه تنظیمات با یکپارچه‌سازی امنیت و تنظیمات عمومی.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase,
    private val observeSecuritySettingsUseCase: ObserveSecuritySettingsUseCase,
    private val clearPinUseCase: ClearPinUseCase,
    private val setBiometricEnabledUseCase: SetBiometricEnabledUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private var persistedSettings: AppSettings = AppSettings()

    init {
        onEvent(SettingsEvent.Load)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Load -> load()
            SettingsEvent.Save -> save()
            SettingsEvent.Reset -> reset()
            SettingsEvent.ClearMessage -> clearMessage()
            is SettingsEvent.ChangeTheme -> updateDraft { copy(theme = event.theme) }
            is SettingsEvent.ChangeCurrency -> updateDraft { copy(currency = event.currency) }
            is SettingsEvent.ChangeLanguage -> updateDraft { copy(language = event.language) }
            is SettingsEvent.ToggleDynamicColor -> updateDraft { copy(dynamicColor = event.enabled) }
            is SettingsEvent.ChangeFirstDay -> updateDraft { copy(firstDayOfWeek = event.day) }

            is SettingsEvent.ToggleNotification -> updateDraft { copy(notificationEnabled = event.enabled) }
            is SettingsEvent.ToggleInstallmentReminder -> updateDraft { copy(installmentReminder = event.enabled) }
            is SettingsEvent.ToggleDailyReminder -> updateDraft { copy(dailyReminder = event.enabled) }
            is SettingsEvent.ToggleBudgetReminder -> updateDraft { copy(budgetReminder = event.enabled) }

            // مدیریت پین
            is SettingsEvent.TogglePin -> {
                if (event.enabled) {
                    // درخواست باز کردن دیالوگ پین
                    _state.update { it.copy(showPinSetup = true) }
                } else {
                    // غیرفعال‌سازی پین و بیومتریک از دیتابیس/دیتاستور امنیت
                    viewModelScope.launch {
                        clearPinUseCase()
                        _state.update { it.copy(pinEnabled = false, biometricEnabled = false) }
                        // همگام‌سازی با فایل تنظیمات عمومی
                        saveGeneralSettingsWithSecurity(pin = false, biometric = false)
                    }
                }
            }
            SettingsEvent.PinSetupDismissed -> {
                _state.update { it.copy(showPinSetup = false) }
            }
            SettingsEvent.PinSetupSuccess -> {
                _state.update { it.copy(showPinSetup = false, pinEnabled = true) }
                viewModelScope.launch {
                    saveGeneralSettingsWithSecurity(pin = true, biometric = _state.value.biometricEnabled)
                }
            }

            // مدیریت بیومتریک
            is SettingsEvent.ToggleBiometric -> {
                if (!_state.value.pinEnabled) {
                    _state.update { it.copy(error = "برای استفاده از اثر انگشت، ابتدا باید قفل PIN را فعال کنید") }
                } else {
                    viewModelScope.launch {
                        setBiometricEnabledUseCase(event.enabled)
                        _state.update { it.copy(biometricEnabled = event.enabled) }
                        saveGeneralSettingsWithSecurity(pin = true, biometric = event.enabled)
                    }
                }
            }
            is SettingsEvent.SetBiometricHardwareAvailable -> {
                _state.update { it.copy(isBiometricHardwareAvailable = event.available) }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val settings = getSettingsUseCase()
                // خواندن آخرین وضعیت واقعی امنیت از ماژول امنیت
                val security = observeSecuritySettingsUseCase().first()

                persistedSettings = settings
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasChanges = false,
                        theme = settings.theme,
                        currency = settings.currency,
                        language = settings.language,
                        biometricEnabled = security.isBiometricEnabled,
                        pinEnabled = security.isPinEnabled,
                        notificationEnabled = settings.notificationEnabled,
                        installmentReminder = settings.installmentReminder,
                        dailyReminder = settings.dailyReminder,
                        budgetReminder = settings.budgetReminder,
                        dynamicColor = settings.dynamicColor,
                        firstDayOfWeek = settings.firstDayOfWeek,
                        error = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "خطا در بارگذاری تنظیمات"
                    )
                }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val draft = stateToSettings(_state.value)
            _state.update { it.copy(isSaving = true, error = null, message = null) }

            runCatching {
                saveSettingsUseCase(draft)
            }.onSuccess {
                persistedSettings = draft
                _state.update {
                    it.copy(
                        isSaving = false,
                        hasChanges = false,
                        message = "تنظیمات با موفقیت ذخیره شد"
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = throwable.message ?: "خطا در ذخیره تنظیمات"
                    )
                }
            }
        }
    }

    private suspend fun saveGeneralSettingsWithSecurity(pin: Boolean, biometric: Boolean) {
        val updatedGeneralSettings = stateToSettings(_state.value).copy(
            pinEnabled = pin,
            biometricEnabled = biometric
        )
        runCatching {
            saveSettingsUseCase(updatedGeneralSettings)
            persistedSettings = updatedGeneralSettings
        }
    }

    private fun reset() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, message = null) }

            runCatching {
                resetSettingsUseCase()
                clearPinUseCase() // ریست کردن تنظیمات امنیت فیزیکی
                getSettingsUseCase()
            }.onSuccess { defaults ->
                persistedSettings = defaults
                _state.update {
                    it.copy(
                        isSaving = false,
                        hasChanges = false,
                        theme = defaults.theme,
                        currency = defaults.currency,
                        language = defaults.language,
                        biometricEnabled = false,
                        pinEnabled = false,
                        notificationEnabled = defaults.notificationEnabled,
                        installmentReminder = defaults.installmentReminder,
                        dailyReminder = defaults.dailyReminder,
                        budgetReminder = defaults.budgetReminder,
                        dynamicColor = defaults.dynamicColor,
                        firstDayOfWeek = defaults.firstDayOfWeek,
                        message = "تنظیمات به حالت پیش‌فرض بازنشانی شد",
                        error = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = throwable.message ?: "خطا در بازنشانی تنظیمات"
                    )
                }
            }
        }
    }

    private fun clearMessage() {
        _state.update { it.copy(message = null, error = null) }
    }

    private fun updateDraft(transform: SettingsState.() -> SettingsState) {
        _state.update { current ->
            val updated = current.transform()
            updated.copy(hasChanges = stateToSettings(updated) != persistedSettings)
        }
    }

    private fun stateToSettings(state: SettingsState): AppSettings {
        return AppSettings(
            theme = state.theme,
            currency = state.currency,
            language = state.language,
            biometricEnabled = state.biometricEnabled,
            pinEnabled = state.pinEnabled,
            notificationEnabled = state.notificationEnabled,
            installmentReminder = state.installmentReminder,
            dailyReminder = state.dailyReminder,
            budgetReminder = state.budgetReminder,
            dynamicColor = state.dynamicColor,
            firstDayOfWeek = state.firstDayOfWeek
        )
    }
}
