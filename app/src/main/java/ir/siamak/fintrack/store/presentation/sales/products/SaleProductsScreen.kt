package ir.siamak.fintrack.store.presentation.sales.products
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft

@Composable fun SaleProductsScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?, onChange: ((SaleDraft) -> SaleDraft) -> Unit) {

                var search by remember { mutableStateOf("") }
                Entry("جست‌وجوی سریع کالا", search, { search = it })
                if(workspace.products.isEmpty()) Text("ابتدا کالا تعریف کنید.")
                workspace.products.filter { it.name.contains(search.trim(), true) }.forEach { product ->
                    val count = draft.quantities[product.id] ?: 0
                    OutlinedCard(onClick = { if(count < product.stock) onChange { it.copy(quantities = it.quantities + (product.id to count + 1)) } }, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.outlinedCardColors(containerColor = if(count > 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            if(product.image != null) Picture(product.image, Modifier.size(48.dp))
                            Column(Modifier.weight(1f)) { Text(product.name, style = MaterialTheme.typography.titleMedium); Text(money(product.price)) }
                            Text("موجودی: " + quantity(product.stock), style = MaterialTheme.typography.labelMedium)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            FilledTonalButton(modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp), enabled = count < product.stock, onClick = { onChange { it.copy(quantities = it.quantities + (product.id to count + 1)) } }) { Text("+") }
                            Text(quantity(count), Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.titleLarge)
                            OutlinedButton(modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp), enabled = count > 0, onClick = { onChange { it.copy(quantities = if(count == 1) it.quantities - product.id else it.quantities + (product.id to count - 1)) } }) { Text("−") }
                        }
                    } }
                }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SaleProductsScreenPreview() { MaterialTheme { StorePage("ثبت فروش") { SaleProductsScreen(SaleDraft(),StoreWorkspace(),null,{}) } } }
