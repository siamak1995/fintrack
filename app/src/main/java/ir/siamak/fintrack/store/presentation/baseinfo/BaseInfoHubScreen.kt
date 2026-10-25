package ir.siamak.fintrack.store.presentation.baseinfo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.store.presentation.*
import ir.siamak.fintrack.store.presentation.components.*
import ir.siamak.fintrack.store.domain.model.*

@Composable fun BaseInfoHubScreen(initialTab: String = "store", vm: StoreFeatureViewModel = hiltViewModel<BaseInfoViewModel>()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    val editing: Any? = when(tab) {
        "store" -> state.data.store
        "seller" -> state.data.sellers.firstOrNull { it.id == editingId }
        "product" -> state.data.products.firstOrNull { it.id == editingId }
        else -> state.data.materials.firstOrNull { it.id == editingId }
    }
    var adding by rememberSaveable { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<Pair<Long,String>?>(null) }
    StorePage("اطلاعات پایه", state.loading, state.error, vm::reload) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("store" to "فروشگاه", "seller" to "فروشنده", "product" to "کالا", "material" to "مواد").forEach { (key,label) ->
                TextButton(onClick = { tab = key }) { Text(if(tab == key) "• " + label else label) }
            }
        }
        if(tab == "store") {
            val store = state.data.store
            if(store == null) Text("ابتدا فروشگاه خود را ثبت کنید.")
            else {
                Picture(store.logoPath)
                Text(store.name, style = MaterialTheme.typography.titleLarge)
                Text(listOf(store.brand, store.phone, store.address, store.description).filterNotNull().joinToString("\n"))
            }
            Button(onClick = { editingId = store?.id; adding = true }) { Text("ثبت / ویرایش فروشگاه") }
        } else {
            Button(onClick = { editingId = null; adding = true }) { Text("افزودن") }
            when(tab) {
                "seller" -> {
                    if(state.data.sellers.isEmpty()) Text("هنوز فروشنده‌ای ثبت نشده است.")
                    state.data.sellers.forEach { value ->
                        CatalogCard(value.fullName, value.phone, { editingId = value.id; adding = true }, { deleting = value.id to value.fullName })
                    }
                }
                "product" -> {
                    if(state.data.products.isEmpty()) Text("هنوز کالایی ثبت نشده است.")
                    state.data.products.forEach { value ->
                        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) {
                            Picture(value.image)
                            CatalogCard(value.name, "موجودی: " + value.stock + " | " + money(value.price), { editingId = value.id; adding = true }, { deleting = value.id to value.name })
                        } }
                    }
                }
                "material" -> {
                    if(state.data.materials.isEmpty()) Text("هنوز ماده اولیه‌ای ثبت نشده است.")
                    state.data.materials.forEach { value ->
                        CatalogCard(value.name, "موجودی: " + value.quantity + " " + value.unit + "\n" + money(value.pricePerUnit) + "\n" + value.purchaseDate.orEmpty().let(::dateLabel),
                            { editingId = value.id; adding = true }, { deleting = value.id to value.name })
                    }
                }
            }
        }
    }
    if(adding) key(tab, editing) {
        val dismiss = { adding = false; vm.clearError() }
        val save: (Any) -> Unit = { value -> vm.save(value) { adding = false } }
        when(tab) {
            "store" -> ir.siamak.fintrack.store.presentation.baseinfo.store.StoreInfoScreen(editing as? Store,state.busy,state.error,{ save(it) },dismiss)
            "seller" -> ir.siamak.fintrack.store.presentation.baseinfo.seller.SellerManagementScreen(editing as? Seller,state.busy,state.error,{ save(it) },dismiss)
            "product" -> ir.siamak.fintrack.store.presentation.baseinfo.product.ProductManagementScreen(editing as? Product,state.busy,state.error,{ save(it) },dismiss)
            else -> ir.siamak.fintrack.store.presentation.baseinfo.material.AddMaterialScreen(editing as? RawMaterial,state.busy,state.error,{ save(it) },dismiss)
        }
    }
    deleting?.let { target -> ConfirmDelete(target.second, { vm.delete(tab, target.first) { deleting = null } }, { deleting = null }) }
}
@Composable private fun CatalogCard(name: String, details: String, edit: () -> Unit, delete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(name, style = MaterialTheme.typography.titleMedium); Text(details)
        Row { TextButton(onClick = edit) { Text("ویرایش") }; TextButton(onClick = delete) { Text("حذف") } }
    } }
}
@Composable internal fun CatalogEditor(kind: String, original: Any?, busy: Boolean, remoteError: String?, save: (Any) -> Unit, dismiss: () -> Unit) {
    val seed = when(original) {
        is Store -> listOf(original.name, original.brand, original.phone.orEmpty(), original.address.orEmpty(), original.description.orEmpty(), original.logoPath.orEmpty())
        is Seller -> listOf(original.firstName, original.lastName, original.phone, original.address.orEmpty(), original.description.orEmpty())
        is Product -> listOf(original.name, original.material.orEmpty(), original.size.orEmpty(), original.weight.orEmpty(), original.stock.toString(), original.price.toString(), original.image.orEmpty())
        is RawMaterial -> listOf(original.name, original.unit, original.pricePerUnit.toString(), original.quantity.toString(), original.purchaseDate.orEmpty(), original.shippingCost?.toString().orEmpty())
        else -> when(kind) { "store" -> List(6) { "" }; "seller" -> List(5) { "" }; "product" -> listOf("","","","","0","0",""); else -> listOf("","","","",java.time.LocalDate.now().toString(),"") }
    }
    var fields by rememberSaveable { mutableStateOf(seed) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val labels = when(kind) {
        "store" -> listOf("نام فروشگاه *", "برند", "تلفن", "آدرس", "توضیحات")
        "seller" -> listOf("نام *", "نام خانوادگی *", "تلفن *", "آدرس", "توضیحات")
        "product" -> listOf("نام کالا *", "جنس", "اندازه", "وزن", "موجودی", "قیمت (تومان)")
        else -> listOf("نام ماده *", "واحد *", "قیمت واحد (تومان) *", "مقدار خرید *", "تاریخ خرید", "هزینه ارسال")
    }
    fun update(index: Int, value: String) { fields = fields.mapIndexed { i, old -> if(i == index) value else old } }
    Dialog(onDismissRequest = { if(!busy) dismiss() }) {
        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
            StorePage("ثبت اطلاعات", error = error ?: remoteError) {
                labels.forEachIndexed { index, label ->
                    if(kind == "material" && index == 4) { DateEntry(label, fields[index]) { update(index,it) }; TextButton(onClick = { update(index,"") }) { Text("بدون تاریخ خرید") } }
                    else Entry(label, fields[index], { update(index,it) }, number = (kind == "product" && index in 4..5) || (kind == "material" && index in listOf(2,3,5)))
                }
                if(kind == "store" || kind == "product") {
                    val index = if(kind == "store") 5 else 6
                    ImageEntry(if(kind == "store") "انتخاب نشان فروشگاه" else "انتخاب تصویر کالا", fields[index].ifBlank { null }) { update(index,it.orEmpty()) }
                }
                Button(enabled = !busy, onClick = {
                    try {
                        fun optional(index: Int) = fields[index].trim().ifBlank { null }
                        val value: Any = when(kind) {
                            "store" -> Store((original as? Store)?.id ?: 1, fields[0].trim(), fields[1].trim(), optional(5), optional(2), optional(3), optional(4))
                            "seller" -> Seller((original as? Seller)?.id ?: 0, fields[0].trim(), fields[1].trim(), fields[2].trim(), optional(3), optional(4))
                            "product" -> {
                                val count = whole(fields[4], true); require(count in 0..Int.MAX_VALUE.toLong())
                                Product((original as? Product)?.id ?: 0, fields[0].trim(), optional(1), optional(2), optional(3), count.toInt(), whole(fields[5], true), optional(6))
                            }
                            else -> RawMaterial((original as? RawMaterial)?.id ?: 0, fields[0].trim(), fields[1].trim(), whole(fields[2]), decimal(fields[3]), optional(4), optional(5)?.let { whole(it) })
                        }
                        save(value)
                    } catch(e: Exception) { error = e.message ?: "اطلاعات نامعتبر است" }
                }, modifier = Modifier.fillMaxWidth()) { Text(if(busy) "در حال ثبت…" else "ذخیره") }
                TextButton(enabled = !busy, onClick = dismiss) { Text("انصراف") }
            }
        }
    }
}
