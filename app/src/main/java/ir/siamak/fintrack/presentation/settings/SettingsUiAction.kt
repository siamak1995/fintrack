package ir.siamak.fintrack.presentation.settings

sealed interface SettingsUiAction {
    data object OpenBackup : SettingsUiAction
    data object OpenRestore : SettingsUiAction
    data object OpenExportPdf : SettingsUiAction
    data object OpenExportExcel : SettingsUiAction
    data object OpenGithub : SettingsUiAction
    data object OpenPrivacy : SettingsUiAction
    data object RateApplication : SettingsUiAction
}
