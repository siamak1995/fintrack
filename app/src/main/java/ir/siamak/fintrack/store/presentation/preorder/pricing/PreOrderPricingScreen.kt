package ir.siamak.fintrack.store.presentation.preorder.pricing
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderPricingScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, materialCost: Long? = null, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                materialCost?.let { Text("هزینه مواد اولیه: " + money(it)) }
                Entry("هزینه تولید", draft.productionCost, { v -> onChange { it.copy(productionCost = v) } }, true)
                Entry("سود", draft.profit, { v -> onChange { it.copy(profit = v) } }, true)
                Entry("هزینه ارسال", draft.shippingCost, { v -> onChange { it.copy(shippingCost = v) } }, true)
                suggested?.let { amount ->
                    Text("قیمت پیشنهادی: " + money(amount))
                    TextButton(onClick = { onChange { it.copy(agreedPrice = amount.toString()) } }) { Text("استفاده از قیمت پیشنهادی") }
                }
                Entry("قیمت توافقی *", draft.agreedPrice, { v -> onChange { it.copy(agreedPrice = v) } }, true)

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderPricingScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderPricingScreen(PreOrderDraft(),StoreWorkspace(),null,null,null,{}) } } }
