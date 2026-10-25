package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.dialogs

import androidx.compose.material3.AlertDialog
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.personalaccountant.data.model.Currency
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections.SectionRadioRow

@Composable
fun CurrencyDialog(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "انتخاب واحد پول")
        },
        text = {
            androidx.compose.foundation.layout.Column {
                Currency.entries.forEach { currency ->
                    SectionRadioRow(
                        title = currency.name,
                        selected = selectedCurrency == currency,
                        onClick = {
                            onCurrencySelected(currency)
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

