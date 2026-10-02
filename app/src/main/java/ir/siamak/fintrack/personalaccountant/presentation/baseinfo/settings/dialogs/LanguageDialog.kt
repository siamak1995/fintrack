package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.AppLanguage
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.SectionRadioRow

@Composable
fun LanguageDialog(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "انتخاب زبان")
        },
        text = {
            androidx.compose.foundation.layout.Column {
                AppLanguage.entries.forEach { language ->
                    SectionRadioRow(
                        title = language.name,
                        selected = selectedLanguage == language,
                        onClick = {
                            onLanguageSelected(language)
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

