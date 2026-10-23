package ir.siamak.fintrack.store.presentation.preorder.materials
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.preorder.create.PreOrderDraft

@Composable fun PreOrderMaterialScreen(draft: PreOrderDraft, workspace: StoreWorkspace, preview: PreOrder?, suggested: Long?, onChange: ((PreOrderDraft) -> PreOrderDraft) -> Unit) {

                Text("مقدار مصرف را در همان واحد ثبت‌شده وارد کنید؛ برای مثال ۲۰۰ گرم از موجودی کیلویی = ۰٫۲ کیلو.")
                if(workspace.materials.isEmpty()) Text("برای سفارش بدون مواد، به مرحله بعد بروید؛ برای مدیریت مواد از اطلاعات پایه استفاده کنید.")
                workspace.materials.forEach { material ->
                    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) {
                        Text(material.name + " — موجودی " + material.quantity + " " + material.unit)
                        Text(money(material.pricePerUnit) + " / " + material.unit)
                        Entry("مقدار مصرف (" + material.unit + ")", draft.quantities[material.id].orEmpty(), { v -> onChange { it.copy(quantities = it.quantities + (material.id to v)) } }, true)
                    } }
                }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun PreOrderMaterialScreenPreview() { MaterialTheme { StorePage("سفارش اختصاصی") { PreOrderMaterialScreen(PreOrderDraft(),StoreWorkspace(),null,null,{}) } } }
