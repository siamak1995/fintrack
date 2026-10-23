package ir.siamak.fintrack.store.presentation.sales.payment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SalePaymentScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                Row { Checkbox(draft.installments, { v -> onChange { it.copy(installments = v) } }); Text("فروش اقساطی (در غیر این صورت نقدی و تسویه)") }
                if(draft.installments) {
                    Entry("تعداد ماه (۱ تا ۶۰)", draft.months, { v -> onChange { it.copy(months = v) } }, true)
                    Entry("درصد سود هر ماه", draft.monthlyPercent, { v -> onChange { it.copy(monthlyPercent = v) } }, true)
                    DateEntry("اولین سررسید", draft.firstDueDate) { v -> onChange { it.copy(firstDueDate = v) } }
                }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SalePaymentScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SalePaymentScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
