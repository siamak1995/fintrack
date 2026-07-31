package ir.siamak.fintrack.presentation.baseinfo.tags.add_edit_tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.toPersian
import ir.siamak.fintrack.presentation.components.FTTopBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTagScreen(
    tagId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditTagViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FTTopBar(
                title = if (tagId == null) "افزودن تگ" else "ویرایش تگ",
                onNavigationClick = onBack
            )
        }
    ) { paddingValues ->
        AddEditTagContent(
            paddingValues = paddingValues,
            state = state,
            onNameChange = { viewModel.onEvent(AddEditTagEvent.NameChanged(it)) },
            onAllowedTypeChange = { viewModel.onEvent(AddEditTagEvent.AllowedTypeChanged(it)) },
            onSaveClick = {
                viewModel.onEvent(AddEditTagEvent.Save)
                onBack()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddEditTagContent(
    paddingValues: PaddingValues,
    state: AddEditTagState,
    onNameChange: (String) -> Unit,
    onAllowedTypeChange: (TransactionType) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .navigationBarsPadding()
            .imePadding()
    ) {
        Text(
            text = "اطلاعات تگ",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("نام برچسب") },
            placeholder = { Text("مثلاً: اجاره، هدیه، خوراک") },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "این تگ در کدام بخش استفاده شود؟",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionType.entries.forEach { type ->
                val isSelected = state.allowedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { onAllowedTypeChange(type) },
                    label = {
                        Text(text = type.toPersian())
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.height(16.dp)
                            )
                        }
                    } else {
                        null
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = state.name.isNotBlank()
        ) {
            Text(text = "ذخیره تغییرات")
        }
    }
}
