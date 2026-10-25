package ir.siamak.fintrack.store.presentation.dashboard

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
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
    var selecting by remember { mutableStateOf(false) }
    StorePage("نمای کلی کسب‌وکار", state.loading, state.error, vm::reload) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if(data.store?.logoPath != null) Picture(data.store?.logoPath, Modifier.size(48.dp))
            else Icon(Icons.Outlined.Storefront, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(data.store?.name ?: "فروشگاه خود را راه‌اندازی کنید", style = MaterialTheme.typography.titleMedium)
                Text(dateLabel(LocalDate.now().toString()), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box {
                TextButton(onClick = { selecting = true }, enabled = data.stores.isNotEmpty() && !state.busy) { Text("تغییر") }
                DropdownMenu(selecting, { selecting = false }) { data.stores.forEach { store ->
                    DropdownMenuItem(text = { Text(store.name + if(store.id == data.store?.id) " ✓" else "") }, onClick = { selecting = false; vm.selectStore(store.id) })
                } }
            }
        }
        if(data.store == null) OutlinedCard(onClick = onCatalog, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("به حسابدار فروشگاه خوش آمدید", style = MaterialTheme.typography.titleMedium)
                Text("ابتدا فروشگاه، برند و کالاهای خود را در اطلاعات پایه ثبت کنید.")
                TextButton(onClick = onCatalog) { Text("شروع راه‌اندازی") }
            }
        }
        Card(onClick = onReports, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("فروش امروز", style = MaterialTheme.typography.labelLarge)
                    Icon(Icons.Outlined.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                }
                Text(money(state.overview.todayAmount), style = MaterialTheme.typography.headlineMedium)
                Text(state.overview.todayCount.toString() + " فاکتور · مشاهده گزارش", style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Metric("سفارش باز", state.overview.openOrders.toString(), Modifier.weight(1f),onPreOrders)
            Metric("کالای ثبت‌شده",data.products.size.toString(),Modifier.weight(1f),onCatalog)
        }
        Text("دسترسی سریع", style = MaterialTheme.typography.titleMedium)
        Button(onClick = onSales, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
            Icon(Icons.Outlined.AddShoppingCart,null); Spacer(Modifier.width(10.dp)); Text("فروش جدید و فاکتورها")
        }
        OutlinedButton(onClick = onPreOrders, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
            Icon(Icons.Outlined.ShoppingBag,null); Spacer(Modifier.width(10.dp)); Text("سفارش اختصاصی")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedCard(onClick = onCatalog,modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Storefront,null); Text("اطلاعات پایه",style = MaterialTheme.typography.labelLarge)
                    Text("فروشگاه، برند و کالا",style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedCard(onClick = onMaterials,modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Inventory2,null); Text("مواد اولیه",style = MaterialTheme.typography.labelLarge)
                    Text("مدیریت از اطلاعات پایه",style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable private fun Metric(label: String, value: String, modifier: Modifier, action: () -> Unit) {
    OutlinedCard(onClick = action, modifier = modifier) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(value,style = MaterialTheme.typography.headlineSmall)
            Text(label,style = MaterialTheme.typography.bodySmall,color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
