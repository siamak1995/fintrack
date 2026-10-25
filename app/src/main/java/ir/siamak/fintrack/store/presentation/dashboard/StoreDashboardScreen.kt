package ir.siamak.fintrack.store.presentation.dashboard
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
@Composable fun StoreDashboardScreen(onSales: () -> Unit = {}, onPreOrders: () -> Unit = {}, onReports: () -> Unit = {}, onCatalog: () -> Unit = {}, onMaterials: () -> Unit = {}, vm: StoreFeatureViewModel = hiltViewModel<StoreDashboardViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val data = state.data
    StorePage(data.store?.name ?: "داشبورد فروشگاه", state.loading, state.error, vm::reload) {
        Text(dateLabel(LocalDate.now().toString()))
        if(data.store == null) Text("برای شروع، از اطلاعات پایه فروشگاه و کالاها را ثبت کنید.")
        Card(onClick = onReports, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("فروش امروز", style = MaterialTheme.typography.labelLarge)
                Text(money(state.overview.todayAmount), style = MaterialTheme.typography.headlineMedium)
                Text("فاکتور امروز: " + state.overview.todayCount + " — مشاهدهٔ گزارش‌ها")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metric("سفارش‌های باز",state.overview.openOrders.toString(),Modifier.weight(1f),onPreOrders)
            Metric("کالاها",data.products.size.toString(),Modifier.weight(1f),onCatalog)
        }
        Metric("مواد اولیه",data.materials.size.toString(),Modifier.fillMaxWidth(),onMaterials)
        Button(onClick = onSales) { Text("ثبت فروش / فاکتورهای فروش") }
        OutlinedButton(onClick = onPreOrders) { Text("سفارش اختصاصی / پیگیری") }
        OutlinedButton(onClick = onReports) { Text("گزارش‌ها و تحلیل فروشگاه") }
    }
}

@Composable private fun Metric(label: String, value: String, modifier: Modifier, action: () -> Unit) {
    OutlinedCard(onClick = action, modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label,style = MaterialTheme.typography.labelLarge)
            Text(value,style = MaterialTheme.typography.headlineSmall)
        }
    }
}
