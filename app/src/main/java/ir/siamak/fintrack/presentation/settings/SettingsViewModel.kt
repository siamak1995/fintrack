package ir.siamak.fintrack.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Currency
import ir.siamak.fintrack.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * مدیریت تنظیمات برنامه
 *
 * مسئول:
 * - بارگذاری تنظیمات
 * - اعمال تغییرات
 * - بروزرسانی State
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())

    val state = _state.asStateFlow()

    init {
        onEvent(SettingsEvent.Load)
    }

    fun onEvent(event: SettingsEvent) {

        when (event) {

            SettingsEvent.Load ->
                load()

            is SettingsEvent.ChangeTheme ->
                updateTheme(event.theme)

            is SettingsEvent.ChangeCurrency ->
                updateCurrency(event.currency)

            is SettingsEvent.ChangeLanguage ->
                updateLanguage(event.language)

            is SettingsEvent.ToggleBiometric ->
                updateBiometric(event.enabled)

            is SettingsEvent.TogglePin ->
                updatePin(event.enabled)

            is SettingsEvent.ToggleNotification ->
                updateNotification(event.enabled)

            is SettingsEvent.ToggleInstallmentReminder ->
                updateInstallmentReminder(event.enabled)

            is SettingsEvent.ToggleDailyReminder ->
                updateDailyReminder(event.enabled)

            is SettingsEvent.ToggleBudgetReminder ->
                updateBudgetReminder(event.enabled)

            is SettingsEvent.ToggleDynamicColor ->
                updateDynamicColor(event.enabled)

            is SettingsEvent.ChangeFirstDay ->
                updateFirstDay(event.day)

            SettingsEvent.Reset ->
                reset()
        }

    }

    /**
     * بارگذاری تنظیمات
     */
    private fun load() {

        viewModelScope.launch {

            _state.update {

                it.copy(
                    isLoading = false,
                    error = null
                )

            }

        }

    }

    /**
     * تغییر تم
     */
    private fun updateTheme(
        theme: ThemeMode
    ) {

        _state.update {

            it.copy(
                theme = theme
            )

        }

    }

    /**
     * تغییر ارز
     */
    private fun updateCurrency(
        currency: Currency
    ) {

        _state.update {

            it.copy(
                currency = currency
            )

        }

    }

    /**
     * تغییر زبان
     */
    private fun updateLanguage(
        language: AppLanguage
    ) {

        _state.update {

            it.copy(
                language = language
            )

        }

    }

    /**
     * اثر انگشت
     */
    private fun updateBiometric(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                biometricEnabled = enabled
            )

        }

    }

    /**
     * پین
     */
    private fun updatePin(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                pinEnabled = enabled
            )

        }

    }

    /**
     * اعلان‌ها
     */
    private fun updateNotification(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                notificationEnabled = enabled
            )

        }

    }

    /**
     * یادآور اقساط
     */
    private fun updateInstallmentReminder(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                installmentReminder = enabled
            )

        }

    }

    /**
     * یادآور روزانه
     */
    private fun updateDailyReminder(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                dailyReminder = enabled
            )

        }

    }

    /**
     * یادآور بودجه
     */
    private fun updateBudgetReminder(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                budgetReminder = enabled
            )

        }

    }

    /**
     * Dynamic Color
     */
    private fun updateDynamicColor(
        enabled: Boolean
    ) {

        _state.update {

            it.copy(
                dynamicColor = enabled
            )

        }

    }

    /**
     * اولین روز هفته
     */
    private fun updateFirstDay(
        day: FirstDayOfWeek
    ) {

        _state.update {
            it.copy(
                firstDayOfWeek = day
            )
        }

    }

    /**
     * بازگردانی تنظیمات
     */
    private fun reset() {

        _state.value = SettingsState()

    }

}