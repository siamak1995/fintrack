package ir.siamak.fintrack.presentation.baseinfo.tags.add_edit_tag

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTTopBar

@Composable
fun AddEditTagScreen(
    tagId: Long?,
    viewModel: AddEditTagViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            FTTopBar(
                title = if (tagId == null) "افزودن تگ" else "ویرایش تگ",
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.value.name,
                onValueChange = { viewModel.onEvent(AddEditTagEvent.NameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("نام تگ") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.onEvent(AddEditTagEvent.Save)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ذخیره")
            }
        }
    }
}
