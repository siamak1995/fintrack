package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.Seller

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerListScreen(
    viewModel: SellerViewModel = hiltViewModel(),
    onAddSellerClick: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("مدیریت فروشندگان") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSellerClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.sellers) { seller ->
                SellerItem(
                    seller = seller,
                    onDelete = { viewModel.onEvent(SellerEvent.DeleteSeller(seller)) }
                )
            }
        }
    }
}

@Composable
fun SellerItem(
    seller: Seller,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${seller.firstName} ${seller.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "تلفن: ${seller.phone}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
