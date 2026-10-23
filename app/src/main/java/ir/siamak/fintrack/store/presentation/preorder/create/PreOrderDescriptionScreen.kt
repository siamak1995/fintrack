package ir.siamak.fintrack.store.presentation.preorder.create
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderDescriptionScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                Entry("شرح کامل سفارش *", draft.description, { v -> onChange { it.copy(description = v) } }, lines = 5)
                DateEntry("تاریخ تحویل", draft.deliveryDate) { v -> onChange { it.copy(deliveryDate = v) } }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderDescriptionScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderDescriptionScreen(PreOrderDraft(),StoreWorkspace(),null,null,{}) } } }
