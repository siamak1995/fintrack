package ir.siamak.fintrack.storeaccountant.presentation.sales.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.Sale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel = hiltViewModel(),
    onCreateSaleClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("فروش فروشگاه") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateSaleClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "فروش جدید")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            SalesSummaryCard(state.todaySalesCount, state.todaySalesAmount)
            
            Text(
                text = "آخرین فروش‌ها",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.sales) { sale ->
                    SaleItem(sale)
                }
            }
        }
    }
}

@Composable
fun SalesSummaryCard(count: Int, amount: Long) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "فروش امروز", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "تعداد فاکتور: $count")
                Text(text = "مبلغ فروش: $amount تومان")
            }
        }
    }
}

@Composable
fun SaleItem(sale: Sale) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = sale.customer.name, style = MaterialTheme.typography.titleSmall)
                Text(text = sale.date, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "${sale.finalAmount} تومان", color = MaterialTheme.colorScheme.primary)
        }
    }
}
