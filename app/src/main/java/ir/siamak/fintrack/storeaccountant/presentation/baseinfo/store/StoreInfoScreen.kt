package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.store

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreInfoScreen(
    viewModel: StoreInfoViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اطلاعات فروشگاه") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(StoreInfoEvent.NameChanged(it)) },
                label = { Text("نام فروشگاه") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.brand,
                onValueChange = { viewModel.onEvent(StoreInfoEvent.BrandChanged(it)) },
                label = { Text("برند") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onEvent(StoreInfoEvent.PhoneChanged(it)) },
                label = { Text("تلفن") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(StoreInfoEvent.AddressChanged(it)) },
                label = { Text("آدرس") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(StoreInfoEvent.DescriptionChanged(it)) },
                label = { Text("توضیحات") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { viewModel.onEvent(StoreInfoEvent.SaveStore) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("ذخیره")
            }
        }
    }
}
