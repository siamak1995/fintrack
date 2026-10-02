package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSellerScreen(
    viewModel: SellerViewModel = hiltViewModel(),
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("افزودن فروشنده") })
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
                value = state.firstName,
                onValueChange = { viewModel.onEvent(SellerEvent.FirstNameChanged(it)) },
                label = { Text("نام") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.lastName,
                onValueChange = { viewModel.onEvent(SellerEvent.LastNameChanged(it)) },
                label = { Text("نام خانوادگی") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onEvent(SellerEvent.PhoneChanged(it)) },
                label = { Text("شماره تماس") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(SellerEvent.AddressChanged(it)) },
                label = { Text("آدرس") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(SellerEvent.DescriptionChanged(it)) },
                label = { Text("توضیحات") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { viewModel.onEvent(SellerEvent.SaveSeller) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.firstName.isNotBlank() && state.lastName.isNotBlank()
            ) {
                Text("ثبت فروشنده")
            }
        }
    }
}
