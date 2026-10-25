package ir.siamak.fintrack.store.presentation.sales.discount
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleDiscountScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(!draft.percentDiscount, { onChange { it.copy(percentDiscount = false) } }, { Text("تخفیف مبلغی") })
                    FilterChip(draft.percentDiscount, { onChange { it.copy(percentDiscount = true) } }, { Text("تخفیف درصدی") })
                }
                Entry("تخفیف " + if(draft.percentDiscount) "(درصد)" else "(تومان)", draft.discount, { v -> onChange { it.copy(discount = v) } }, true)
                Entry("مالیات (درصد)", draft.tax, { v -> onChange { it.copy(tax = v) } }, true)

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleDiscountScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleDiscountScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
