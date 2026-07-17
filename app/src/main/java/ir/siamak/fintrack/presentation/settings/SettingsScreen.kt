package ir.siamak.fintrack.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.settings.sections.AboutSection
import ir.siamak.fintrack.presentation.settings.sections.AppearanceSection
import ir.siamak.fintrack.presentation.settings.sections.GeneralSection
import ir.siamak.fintrack.presentation.settings.sections.NotificationSection
import ir.siamak.fintrack.presentation.settings.sections.SecuritySection

/**
 * محتوای صفحه تنظیمات.
 */
@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message, state.error) {
        val text = state.message ?: state.error
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            onEvent(SettingsEvent.ClearMessage)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (state.hasChanges) {
                ExtendedFloatingActionButton(
                    onClick = { onEvent(SettingsEvent.Save) },
                    expanded = true
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Text("ذخیره تنظیمات")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (state.isSaving || state.isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillParentMaxWidth())
                }
            }

            item {
                AppearanceSection(
                    theme = state.theme,
                    dynamicColor = state.dynamicColor,
                    currency = state.currency,
                    language = state.language,
                    onThemeChanged = { onEvent(SettingsEvent.ChangeTheme(it)) },
                    onCurrencyChanged = { onEvent(SettingsEvent.ChangeCurrency(it)) },
                    onLanguageChanged = { onEvent(SettingsEvent.ChangeLanguage(it)) },
                    onDynamicColorChanged = { onEvent(SettingsEvent.ToggleDynamicColor(it)) }
                )
            }

            item {
                SecuritySection(
                    biometricEnabled = state.biometricEnabled,
                    pinEnabled = state.pinEnabled,
                    onBiometricChanged = { onEvent(SettingsEvent.ToggleBiometric(it)) },
                    onPinChanged = { onEvent(SettingsEvent.TogglePin(it)) }
                )
            }

            item {
                NotificationSection(
                    notificationEnabled = state.notificationEnabled,
                    installmentReminder = state.installmentReminder,
                    dailyReminder = state.dailyReminder,
                    budgetReminder = state.budgetReminder,
                    onNotificationChanged = { onEvent(SettingsEvent.ToggleNotification(it)) },
                    onInstallmentChanged = { onEvent(SettingsEvent.ToggleInstallmentReminder(it)) },
                    onDailyChanged = { onEvent(SettingsEvent.ToggleDailyReminder(it)) },
                    onBudgetChanged = { onEvent(SettingsEvent.ToggleBudgetReminder(it)) }
                )
            }

            item {
                GeneralSection(
                    firstDay = state.firstDayOfWeek,
                    onFirstDayChanged = { onEvent(SettingsEvent.ChangeFirstDay(it)) }
                )
            }

            item {
                AboutSection(version = state.appVersion)
            }

            item {
                ExtendedFloatingActionButton(
                    onClick = { onEvent(SettingsEvent.Reset) },
                    expanded = true
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null
                    )
                    Text("بازنشانی به پیش‌فرض")
                }
            }
        }
    }
}
