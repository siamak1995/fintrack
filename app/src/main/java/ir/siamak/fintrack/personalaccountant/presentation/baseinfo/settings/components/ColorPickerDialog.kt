package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.components

import androidx.compose.material3.AlertDialog
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "انتخاب رنگ")
        },
        text = {
            Text(text = "فعلا انتخاب رنگ سفارشی پیاده‌سازی نشده است.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                }
            ) {
                Text(text = "تایید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "انصراف")
            }
        }
    )
}

