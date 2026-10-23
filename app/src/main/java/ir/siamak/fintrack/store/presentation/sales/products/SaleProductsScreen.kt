package ir.siamak.fintrack.store.presentation.sales.products
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleProductsScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                if(workspace.products.isEmpty()) Text("ابتدا کالا تعریف کنید.")
                workspace.products.forEach { product ->
                    val count = draft.quantities[product.id] ?: 0
                    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) {
                        Picture(product.image)
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Text("موجودی: " + product.stock + " | " + money(product.price))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(enabled = count < product.stock, onClick = { onChange { it.copy(quantities = it.quantities + (product.id to count + 1)) } }) { Text("+") }
                            Text(count.toString())
                            OutlinedButton(enabled = count > 0, onClick = { onChange { it.copy(quantities = if(count == 1) it.quantities - product.id else it.quantities + (product.id to count - 1)) } }) { Text("−") }
                        }
                    } }
                }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleProductsScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleProductsScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
