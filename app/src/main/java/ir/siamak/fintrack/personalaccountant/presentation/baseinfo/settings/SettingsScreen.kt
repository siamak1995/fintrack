package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings

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
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.AboutSection
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.AppearanceSection
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.GeneralSection
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.NotificationSection
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.SecuritySection

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
                    text = { Text("ذخیره تنظیمات") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )
                    },
                    onClick = { onEvent(SettingsEvent.Save) },
                    expanded = true
                )
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
                    isBiometricHardwareAvailable = state.isBiometricHardwareAvailable,
                    onBiometricChanged = { enabled ->
                        onEvent(SettingsEvent.ToggleBiometric(enabled))
                    },
                    onPinChanged = { enabled ->
                        onEvent(SettingsEvent.TogglePin(enabled))
                    }
                )
            }

//            item {
//                NotificationSection(
//                    notificationEnabled = state.notificationEnabled,
//                    installmentReminder = state.installmentReminder,
//                    dailyReminder = state.dailyReminder,
//                    budgetReminder = state.budgetReminder,
//                    onNotificationChanged = { onEvent(SettingsEvent.ToggleNotification(it)) },
//                    onInstallmentChanged = { onEvent(SettingsEvent.ToggleInstallmentReminder(it)) },
//                    onDailyChanged = { onEvent(SettingsEvent.ToggleDailyReminder(it)) },
//                    onBudgetChanged = { onEvent(SettingsEvent.ToggleBudgetReminder(it)) }
//                )
//            }

//            item {
//                GeneralSection(
//                    firstDay = state.firstDayOfWeek,
//                    onFirstDayChanged = { onEvent(SettingsEvent.ChangeFirstDay(it)) }
//                )
//            }

            item {
                AboutSection(version = state.appVersion)
            }

            item {
                ExtendedFloatingActionButton(
                    text = { Text("بازنشانی به پیش‌فرض") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null
                        )
                    },
                    onClick = { onEvent(SettingsEvent.Reset) },
                    expanded = true
                )
            }
        }
    }
}

