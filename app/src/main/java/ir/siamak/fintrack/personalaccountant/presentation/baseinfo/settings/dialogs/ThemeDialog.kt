package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.SectionRadioRow
import ir.siamak.fintrack.personalaccountant.presentation.theme.ThemeMode

@Composable
fun ThemeDialog(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "انتخاب تم")
        },
        text = {
            androidx.compose.foundation.layout.Column {
                ThemeMode.entries.forEach { theme ->
                    SectionRadioRow(
                        title = theme.name,
                        selected = selectedTheme == theme,
                        onClick = {
                            onThemeSelected(theme)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "بستن")
            }
        }
    )
}

