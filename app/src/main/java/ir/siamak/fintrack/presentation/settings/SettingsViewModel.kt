package ir.siamak.fintrack.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.domain.settings.AppSettings
import ir.siamak.fintrack.domain.settings.GetSettingsUseCase
import ir.siamak.fintrack.domain.settings.ResetSettingsUseCase
import ir.siamak.fintrack.domain.settings.SaveSettingsUseCase
import ir.siamak.fintrack.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        onEvent(SettingsEvent.Load)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Load -> load()
            is SettingsEvent.ChangeTheme -> updateTheme(event.theme)
            is SettingsEvent.ChangeCurrency -> updateCurrency(event.currency)
            is SettingsEvent.ChangeLanguage -> updateLanguage(event.language)
            is SettingsEvent.ToggleBiometric -> updateBiometric(event.enabled)
            is SettingsEvent.TogglePin -> updatePin(event.enabled)
            is SettingsEvent.ToggleNotification -> updateNotification(event.enabled)
            is SettingsEvent.ToggleInstallmentReminder -> updateInstallmentReminder(event.enabled)
            is SettingsEvent.ToggleDailyReminder -> updateDailyReminder(event.enabled)
            is SettingsEvent.ToggleBudgetReminder -> updateBudgetReminder(event.enabled)
            is SettingsEvent.ToggleDynamicColor -> updateDynamicColor(event.enabled)
            is SettingsEvent.ChangeFirstDay -> updateFirstDay(event.day)
            SettingsEvent.Reset -> reset()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val settings = getSettingsUseCase()
            _state.update {
                it.copy(
                    isLoading = false,
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
        }
    }

    private fun persistState() {
        viewModelScope.launch {
            val current = _state.value
            saveSettingsUseCase(
                AppSettings(
                    theme = current.theme,
                    currency = current.currency,
                    language = current.language,
                    biometricEnabled = current.biometricEnabled,
                    pinEnabled = current.pinEnabled,
                    notificationEnabled = current.notificationEnabled,
                    installmentReminder = current.installmentReminder,
                    dailyReminder = current.dailyReminder,
                    budgetReminder = current.budgetReminder,
                    dynamicColor = current.dynamicColor,
                    firstDayOfWeek = current.firstDayOfWeek
                )
            )
        }
    }

    private fun updateTheme(theme: ThemeMode) {
        _state.update { it.copy(theme = theme) }
        persistState()
    }

    private fun updateCurrency(currency: Currency) {
        _state.update { it.copy(currency = currency) }
        persistState()
    }

    private fun updateLanguage(language: AppLanguage) {
        _state.update { it.copy(language = language) }
        persistState()
    }

    private fun updateBiometric(enabled: Boolean) {
        _state.update { it.copy(biometricEnabled = enabled) }
        persistState()
    }

    private fun updatePin(enabled: Boolean) {
        _state.update { it.copy(pinEnabled = enabled) }
        persistState()
    }

    private fun updateNotification(enabled: Boolean) {
        _state.update { it.copy(notificationEnabled = enabled) }
        persistState()
    }

    private fun updateInstallmentReminder(enabled: Boolean) {
        _state.update { it.copy(installmentReminder = enabled) }
        persistState()
    }

    private fun updateDailyReminder(enabled: Boolean) {
        _state.update { it.copy(dailyReminder = enabled) }
        persistState()
    }

    private fun updateBudgetReminder(enabled: Boolean) {
        _state.update { it.copy(budgetReminder = enabled) }
        persistState()
    }

    private fun updateDynamicColor(enabled: Boolean) {
        _state.update { it.copy(dynamicColor = enabled) }
        persistState()
    }

    private fun updateFirstDay(day: FirstDayOfWeek) {
        _state.update { it.copy(firstDayOfWeek = day) }
        persistState()
    }

    private fun reset() {
        viewModelScope.launch {
            resetSettingsUseCase()
            load()
        }
    }
}
