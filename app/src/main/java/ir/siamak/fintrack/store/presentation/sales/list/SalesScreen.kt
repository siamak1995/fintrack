package ir.siamak.fintrack.store.presentation.sales.list
import androidx.compose.material3.*
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
        Text("فروش امروز: " + money(state.overview.todayAmount))
        Text("تعداد فاکتور: " + state.overview.todayCount)
        Button(onClick = onCreate) { Text("ثبت فروش جدید") }
        OutlinedButton(onClick = onInvoices) { Text("فاکتورهای فروش") }
        Text("آخرین فروش‌ها", style = MaterialTheme.typography.titleMedium)
        if(state.data.sales.isEmpty()) Text("هنوز فروشی ثبت نشده است.")
        state.data.sales.take(30).forEach { Text(dateLabel(it.createdAt) + " — " + money(it.finalAmount) + " — " + it.items.joinToString { item -> item.productName }) }
    }
}
