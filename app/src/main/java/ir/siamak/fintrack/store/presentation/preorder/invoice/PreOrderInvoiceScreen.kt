package ir.siamak.fintrack.store.presentation.preorder.invoice
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderInvoiceScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                Text("مشتری: " + draft.name); Text(draft.description)
                Text("تحویل: " + dateLabel(draft.deliveryDate))
                preview?.items?.forEach { DataRow(it.materialName, quantity(it.quantity)) }
                Text("با تأیید، مواد مصرفی از موجودی کسر می‌شوند.")

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderInvoiceScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderInvoiceScreen(PreOrderDraft(),StoreWorkspace(),null,null,{}) } } }
