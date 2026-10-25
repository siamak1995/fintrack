package ir.siamak.fintrack.store.presentation.preorder.customer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderCustomerScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                Entry("نام مشتری *", draft.name, { v -> onChange { it.copy(name = v) } })
                Entry("تلفن *", draft.phone, { v -> onChange { it.copy(phone = v) } })
                Entry("آدرس", draft.address, { v -> onChange { it.copy(address = v) } })
                Text(workspace.store?.name ?: "فروشگاه انتخاب نشده", style = MaterialTheme.typography.labelLarge)
                Text("فروشنده و امضای فاکتور", style = MaterialTheme.typography.titleSmall)
                FilterChip(draft.sellerId == null, { onChange { it.copy(sellerId = null) } }, { Text("بدون فروشنده") })
                workspace.sellers.forEach { seller -> FilterChip(draft.sellerId == seller.id, { onChange { it.copy(sellerId = seller.id) } }, { Text(seller.fullName) }) }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderCustomerScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderCustomerScreen(PreOrderDraft(),StoreWorkspace(),null,null,{}) } } }
