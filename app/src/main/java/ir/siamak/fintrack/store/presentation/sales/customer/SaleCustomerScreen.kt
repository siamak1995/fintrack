package ir.siamak.fintrack.store.presentation.sales.customer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleCustomerScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                Entry("نام مشتری *", draft.name, { v -> onChange { it.copy(name = v) } })
                Entry("تلفن *", draft.phone, { v -> onChange { it.copy(phone = v) } })
                Entry("آدرس", draft.address, { v -> onChange { it.copy(address = v) } })
                Text("فروشنده")
                FilterChip(selected = draft.sellerId == null, onClick = { onChange { it.copy(sellerId = null) } }, label = { Text("بدون فروشنده") })
                workspace.sellers.forEach { seller ->
                    FilterChip(selected = draft.sellerId == seller.id, onClick = { onChange { it.copy(sellerId = seller.id) } }, label = { Text(seller.fullName) })
                }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleCustomerScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleCustomerScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
