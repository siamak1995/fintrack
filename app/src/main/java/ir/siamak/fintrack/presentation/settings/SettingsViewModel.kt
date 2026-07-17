package ir.siamak.fintrack.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.domain.settings.GetSettingsUseCase
import ir.siamak.fintrack.domain.settings.ResetSettingsUseCase
import ir.siamak.fintrack.domain.settings.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel صفحه تنظیمات.
 *
 * رفتار:
 * - هنگام ورود، تنظیمات ذخیره‌شده را load می‌کند
 * - تغییرات را ابتدا در state نگه می‌دارد
 * - فقط با رویداد Save آن‌ها را persist می‌کند
 * - Reset تنظیمات را به پیش‌فرض برمی‌گرداند و ذخیره می‌کند
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase
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
            is SettingsEvent.ToggleBiometric -> updateDraft { copy(biometricEnabled = event.enabled) }
            is SettingsEvent.TogglePin -> updateDraft { copy(pinEnabled = event.enabled) }
            is SettingsEvent.ToggleNotification -> updateDraft { copy(notificationEnabled = event.enabled) }
            is SettingsEvent.ToggleInstallmentReminder -> updateDraft { copy(installmentReminder = event.enabled) }
            is SettingsEvent.ToggleDailyReminder -> updateDraft { copy(dailyReminder = event.enabled) }
            is SettingsEvent.ToggleBudgetReminder -> updateDraft { copy(budgetReminder = event.enabled) }
            is SettingsEvent.ToggleDynamicColor -> updateDraft { copy(dynamicColor = event.enabled) }
            is SettingsEvent.ChangeFirstDay -> updateDraft { copy(firstDayOfWeek = event.day) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                getSettingsUseCase()
            }.onSuccess { settings ->
                persistedSettings = settings
                _state.update {
                    it.copy(
                        isLoading = false,
                        hasChanges = false,
                        theme = settings.theme,
                        currency = settings.currency,
                        language = settings.language,
                        biometricEnabled = settings.biometricEnabled,
                        pinEnabled = settings.pinEnabled,
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

            _state.update {
                it.copy(
                    isSaving = true,
                    error = null,
                    message = null
                )
            }

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

    private fun reset() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    error = null,
                    message = null
                )
            }

            runCatching {
                resetSettingsUseCase()
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
                        biometricEnabled = defaults.biometricEnabled,
                        pinEnabled = defaults.pinEnabled,
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
