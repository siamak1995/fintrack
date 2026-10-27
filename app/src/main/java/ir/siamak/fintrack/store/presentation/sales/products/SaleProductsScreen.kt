package ir.siamak.fintrack.store.presentation.sales.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.presentation.sales.create.SaleDraft
import kotlinx.coroutines.launch

@Composable
fun SaleProductsScreen(draft: SaleDraft, workspace: StoreWorkspace, preview: Sale?,
    onChange: ((SaleDraft) -> SaleDraft) -> Unit,
    onAddProduct: (suspend (Product) -> Result<Unit>)? = null
) {
    var selecting by rememberSaveable { mutableStateOf(false) }
    var adding by rememberSaveable { mutableStateOf(false) }
    fun select(product: Product) {
        val count = draft.quantities[product.id] ?: 0
        if(count < product.stock) onChange { it.copy(quantities = it.quantities + (product.id to count + 1)) }
    }
    FormSection("کالاهای فروش") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { selecting = true }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) { Text("انتخاب محصول") }
            FilledTonalButton(onClick = { adding = true }, enabled = onAddProduct != null,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)) { Text("افزودن محصول") }
        }
        val selected = workspace.products.filter { (draft.quantities[it.id] ?: 0) > 0 }
        if(selected.isEmpty()) Text("محصولی به سبد اضافه نشده است.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        selected.forEach { product -> key(product.id) {
            val count = draft.quantities[product.id] ?: 0
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.image?.let { Picture(it, Modifier.size(42.dp)) }
                        Column(Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(money(product.price) + product.color?.let { " · رنگ: " + it }.orEmpty(), style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onChange { it.copy(quantities = it.quantities - product.id) } }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalButton(enabled = count < product.stock, onClick = { select(product) }, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) { Text("+") }
                        OutlinedTextField(value = count.toString(), onValueChange = { value ->
                            val raw = numeric(value)
                            val next = if(raw.isBlank()) 0 else raw.toIntOrNull()
                            if(next != null && next in 0..product.stock) onChange { it.copy(quantities = if(next == 0) it.quantities - product.id else it.quantities + (product.id to next)) }
                        }, label = { Text("تعداد") }, singleLine = true, modifier = Modifier.weight(1f),
                            visualTransformation = ir.siamak.fintrack.common.core.formatter.GroupedNumberTransformation,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                        OutlinedButton(onClick = { onChange { it.copy(quantities = if(count <= 1) it.quantities - product.id else it.quantities + (product.id to count - 1)) } }, modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) { Text("−") }
                    }
                    Text("موجودی: " + quantity(product.stock) + " · جمع: " + money(Math.multiplyExact(product.price,count.toLong())), style = MaterialTheme.typography.labelMedium)
                }
            }
        } }
    }
    if(selecting) {
        var query by rememberSaveable { mutableStateOf("") }
        var limit by rememberSaveable(query) { mutableIntStateOf(40) }
        val matches = remember(workspace.products, query) {
            val needle = productNameKey(query)
            workspace.products.filter { needle.isBlank() || productNameKey(it.name + " " + it.color.orEmpty()).contains(needle) }
        }
        AlertDialog(onDismissRequest = { selecting = false }, title = { Text("انتخاب محصول") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Entry("جست‌وجوی نام یا رنگ", query, { query = it })
                Text(quantity(matches.size) + " محصول", style = MaterialTheme.typography.labelMedium)
                LazyColumn(Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 360.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(matches.take(limit), key = { it.id }) { product ->
                        val count = draft.quantities[product.id] ?: 0
                        OutlinedCard(onClick = { if(count < product.stock) { select(product); selecting = false } }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(product.name, style = MaterialTheme.typography.titleSmall)
                                Text(money(product.price) + " · موجودی: " + quantity(product.stock), style = MaterialTheme.typography.bodySmall)
                                product.color?.let { Text("رنگ: " + it, style = MaterialTheme.typography.bodySmall) }
                                if(product.stock <= count) Text("موجودی قابل انتخاب ندارد", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    if(matches.isEmpty()) item { Text("محصولی پیدا نشد.") }
                    if(matches.size > limit) item { TextButton(onClick = { limit += 40 }, modifier = Modifier.fillMaxWidth()) { Text("نمایش محصولات بیشتر") } }
                }
                if(onAddProduct != null) TextButton(onClick = { selecting = false; adding = true }) { Text("محصول پیدا نشد؟ افزودن محصول") }
            }
        }, confirmButton = { TextButton(onClick = { selecting = false }) { Text("بستن") } })
    }
    if(adding && onAddProduct != null) QuickProductDialog(onAddProduct) { adding = false }
}

@Composable private fun QuickProductDialog(save: suspend (Product) -> Result<Unit>, dismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var stock by rememberSaveable { mutableStateOf("1") }
    var color by rememberSaveable { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    AlertDialog(onDismissRequest = { if(!busy) dismiss() }, title = { Text("افزودن سریع محصول") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Entry("نام محصول *", name, { name = it })
            Entry("قیمت واحد · تومان *", price, { price = it }, number = true)
            Entry("موجودی *", stock, { stock = it }, number = true)
            Entry("رنگ (اختیاری)", color, { color = it })
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }, confirmButton = {
        Button(enabled = !busy && name.isNotBlank() && price.isNotBlank(), onClick = {
            scope.launch {
                busy = true; error = null
                try {
                    val count = whole(stock); require(count in 1..Int.MAX_VALUE.toLong()) { "موجودی باید حداقل یک باشد" }
                    save(Product(name = name.trim(), stock = count.toInt(), price = whole(price), color = color.trim().ifBlank { null })).getOrThrow()
                    dismiss()
                } catch(cancel: kotlinx.coroutines.CancellationException) { throw cancel }
                catch(e: Exception) { error = e.message ?: "ثبت محصول ناموفق بود" }
                finally { busy = false }
            }
        }) { Text(if(busy) "در حال ثبت…" else "ثبت و افزودن به سبد") }
    }, dismissButton = { TextButton(enabled = !busy, onClick = dismiss) { Text("انصراف") } })
}
