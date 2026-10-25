package ir.siamak.fintrack.store.presentation.sales.list
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.*
import ir.siamak.fintrack.store.presentation.components.*
import java.time.LocalDate
@Composable fun SalesScreen(onInvoices: () -> Unit = {}, onCreate: () -> Unit = {}, vm: StoreFeatureViewModel = hiltViewModel<SalesViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    StorePage("فروش", state.loading, state.error, vm::reload) {
        Text(state.data.store?.name ?: "ابتدا فروشگاه را تعریف کنید", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("فروش امروز",style = MaterialTheme.typography.labelLarge)
                Text(money(state.overview.todayAmount),style = MaterialTheme.typography.headlineSmall)
                Text(state.overview.todayCount.toString() + " فاکتور")
            }
        }
        Button(onClick = onCreate, enabled = state.data.store != null, modifier = Modifier.fillMaxWidth()) { Text("ثبت فروش جدید") }
        OutlinedButton(onClick = onInvoices, modifier = Modifier.fillMaxWidth()) { Text("فاکتورهای فروش") }
        Text("آخرین فروش‌ها", style = MaterialTheme.typography.titleMedium)
        if(state.data.sales.isEmpty()) Text("هنوز فروشی ثبت نشده است.")
        state.data.sales.take(30).forEach { Text(dateLabel(it.createdAt) + " — " + money(it.finalAmount) + " — " + it.items.joinToString { item -> item.productName }) }
    }
}
