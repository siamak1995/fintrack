package ir.siamak.fintrack.store.presentation.sales.invoice
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleInvoiceScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                Text("مشتری: " + draft.name)
                preview?.items?.forEach { Text(it.productName + " × " + it.quantity + " — " + money(it.unitPrice)) }
                Text("با ثبت نهایی، موجودی کسر و فاکتور ذخیره می‌شود.")

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleInvoiceScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleInvoiceScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
