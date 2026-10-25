package ir.siamak.fintrack.store.presentation.preorder.payment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderPaymentScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                Row { Checkbox(draft.advanceIsPercent, { v -> onChange { it.copy(advanceIsPercent = v) } }); Text("پیش‌پرداخت درصدی") }
                Entry(if(draft.advanceIsPercent) "درصد پیش‌پرداخت" else "مبلغ پیش‌پرداخت (تومان)", draft.advance, { v -> onChange { it.copy(advance = v) } }, true)

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderPaymentScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderPaymentScreen(PreOrderDraft(),StoreWorkspace(),null,null,{}) } } }
