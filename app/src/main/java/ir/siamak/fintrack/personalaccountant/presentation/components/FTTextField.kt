package ir.siamak.fintrack.personalaccountant.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FTTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        visualTransformation = if(keyboardOptions.keyboardType in listOf(androidx.compose.ui.text.input.KeyboardType.Number, androidx.compose.ui.text.input.KeyboardType.Decimal) && ',' !in value && '٬' !in value) ir.siamak.fintrack.common.core.formatter.GroupedNumberTransformation else ir.siamak.fintrack.common.core.formatter.PersianVisualTransformation,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        supportingText = if(label.contains("مبلغ") && value.isNotBlank()) ({ Text(ir.siamak.fintrack.common.core.formatter.PersianAmountWords.format(value,"تومان")) }) else null,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}

