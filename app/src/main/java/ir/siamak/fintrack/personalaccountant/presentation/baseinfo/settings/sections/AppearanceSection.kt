package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Currency
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.AppLanguage
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.toDisplayName
import ir.siamak.fintrack.personalaccountant.presentation.theme.ThemeMode

@Composable
fun AppearanceSection(
    theme: ThemeMode,
    dynamicColor: Boolean,
    currency: Currency,
    language: AppLanguage,
    onThemeChanged: (ThemeMode) -> Unit,
    onCurrencyChanged: (Currency) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit
) {
    SectionCard(title = "ظاهر و نمایش") {
        Text(
            text = "تم برنامه",
            style = MaterialTheme.typography.titleSmall
        )

        ThemeMode.entries.forEach { item ->
            SectionRadioRow(
                title = item.toDisplayName(),
                selected = theme == item,
                onClick = { onThemeChanged(item) }
            )
        }

        SectionSwitchRow(
            title = "رنگ پویا",
            checked = dynamicColor,
            onCheckedChange = onDynamicColorChanged
        )

        EnumDropdownField(
            title = "واحد پول",
            selectedText = currency.toDisplayName(),
            options = Currency.entries,
            optionLabel = { it.toDisplayName() },
            onSelected = onCurrencyChanged
        )

        EnumDropdownField(
            title = "زبان",
            selectedText = language.toDisplayName(),
            options = AppLanguage.entries,
            optionLabel = { it.toDisplayName() },
            onSelected = onLanguageChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdownField(
    title: String,
    selectedText: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(item)) },
                        onClick = {
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

