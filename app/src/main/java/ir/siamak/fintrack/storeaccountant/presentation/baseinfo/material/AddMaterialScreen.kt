package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun AddMaterialScreen(
    viewModel: MaterialViewModel = hiltViewModel(),
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
            TopAppBar(title = { Text("افزودن ماده اولیه") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(MaterialEvent.NameChanged(it)) },
                label = { Text("نام ماده *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.unit,
                onValueChange = { viewModel.onEvent(MaterialEvent.UnitChanged(it)) },
                label = { Text("نوع واحد *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.pricePerUnit,
                onValueChange = { viewModel.onEvent(MaterialEvent.PriceChanged(it)) },
                label = { Text("قیمت واحد *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.quantity,
                onValueChange = { viewModel.onEvent(MaterialEvent.QuantityChanged(it)) },
                label = { Text("مقدار خرید *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.purchaseDate,
                onValueChange = { viewModel.onEvent(MaterialEvent.PurchaseDateChanged(it)) },
                label = { Text("تاریخ خرید") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.shippingCost,
                onValueChange = { viewModel.onEvent(MaterialEvent.ShippingCostChanged(it)) },
                label = { Text("هزینه ارسال") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.onEvent(MaterialEvent.SaveMaterial) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank() && state.unit.isNotBlank()
            ) {
                Text("ثبت ماده اولیه")
            }
        }
    }
}
