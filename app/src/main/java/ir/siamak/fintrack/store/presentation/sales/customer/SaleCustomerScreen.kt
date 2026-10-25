package ir.siamak.fintrack.store.presentation.sales.customer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleCustomerScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

            FormSection("مشخصات مشتری") {
                if(workspace.invoices.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    workspace.invoices.sortedByDescending { it.id }.map { it.customer }.distinctBy { it.phone }.take(6).forEach { customer ->
                        AssistChip(onClick = { onChange { it.copy(name = customer.name, phone = customer.phone, address = customer.address.orEmpty()) } }, label = { Text(customer.name) })
                    }
                }
                Entry("نام مشتری *", draft.name, { v -> onChange { it.copy(name = v) } })
                Entry("تلفن *", draft.phone, { v -> onChange { it.copy(phone = v) } })
                Entry("آدرس", draft.address, { v -> onChange { it.copy(address = v) } })
            }
            FormSection("فروشگاه و فروشنده") {
                Text(workspace.store?.name ?: "فروشگاه انتخاب نشده", style = MaterialTheme.typography.labelLarge)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = draft.sellerId == null, onClick = { onChange { it.copy(sellerId = null) } }, label = { Text("بدون فروشنده") })
                workspace.sellers.forEach { seller ->
                    FilterChip(selected = draft.sellerId == seller.id, onClick = { onChange { it.copy(sellerId = seller.id) } }, label = { Text(seller.fullName + if(seller.signaturePath != null) " · امضا" else "") })
                }
                }
                workspace.sellers.firstOrNull { it.id == draft.sellerId }?.signaturePath?.let { Picture(it) }
            }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleCustomerScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleCustomerScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
