package ir.siamak.fintrack.store.presentation.dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.*
import ir.siamak.fintrack.store.presentation.components.*
import java.time.LocalDate
@Composable fun StoreDashboardScreen(onSales: () -> Unit = {}, onPreOrders: () -> Unit = {}, vm: StoreFeatureViewModel = hiltViewModel<StoreDashboardViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val data = state.data
    StorePage(data.store?.name ?: "داشبورد فروشگاه", state.loading, state.error, vm::reload) {
        Text(dateLabel(LocalDate.now().toString()))
        if(data.store == null) Text("برای شروع، از اطلاعات پایه فروشگاه و کالاها را ثبت کنید.")
        Text("فروش امروز: " + money(state.overview.todayAmount))
        Text("فاکتور امروز: " + state.overview.todayCount)
        Text("کالاها: " + data.products.size + " | مواد اولیه: " + data.materials.size)
        Text("سفارش‌های باز: " + state.overview.openOrders)
        Button(onClick = onSales) { Text("ثبت فروش / فاکتورهای فروش") }
        OutlinedButton(onClick = onPreOrders) { Text("سفارش اختصاصی / پیگیری") }
    }
}
