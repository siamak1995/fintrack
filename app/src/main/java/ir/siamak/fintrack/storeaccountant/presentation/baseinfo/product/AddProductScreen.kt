package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product

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
fun AddProductScreen(
    viewModel: ProductViewModel = hiltViewModel(),
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
            TopAppBar(title = { Text("افزودن کالا") })
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
                onValueChange = { viewModel.onEvent(ProductEvent.NameChanged(it)) },
                label = { Text("نام کالا *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.material,
                onValueChange = { viewModel.onEvent(ProductEvent.MaterialChanged(it)) },
                label = { Text("جنس") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.size,
                onValueChange = { viewModel.onEvent(ProductEvent.SizeChanged(it)) },
                label = { Text("سایز") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.weight,
                onValueChange = { viewModel.onEvent(ProductEvent.WeightChanged(it)) },
                label = { Text("وزن") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.stock,
                onValueChange = { viewModel.onEvent(ProductEvent.StockChanged(it)) },
                label = { Text("موجودی اولیه") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.onEvent(ProductEvent.PriceChanged(it)) },
                label = { Text("قیمت فروش") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.onEvent(ProductEvent.SaveProduct) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank()
            ) {
                Text("ثبت کالا")
            }
        }
    }
}
