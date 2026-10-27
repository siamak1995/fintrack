package ir.siamak.fintrack.store.presentation.baseinfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
        "store" -> state.data.stores.firstOrNull { it.id == editingId }
        "brand" -> null
        "seller" -> state.data.sellers.firstOrNull { it.id == editingId }
        "product" -> state.data.products.firstOrNull { it.id == editingId }
        else -> state.data.materials.firstOrNull { it.id == editingId }
    }
    var adding by rememberSaveable { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<Brand?>(null) }
    var deletingBrand by remember { mutableStateOf<Brand?>(null) }
    var deleting by remember { mutableStateOf<Pair<Long,String>?>(null) }
    StorePage("اطلاعات پایه", state.loading, state.error, vm::reload) {
        Text("فهرست‌ها و هویت کسب‌وکار", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("store" to "فروشگاه‌ها", "brand" to "برندها", "seller" to "فروشندگان", "product" to "کالاها", "material" to "مواد اولیه").forEach { (key,label) ->
                FilterChip(selected = tab == key, onClick = { tab = key }, label = { Text(label) })
            }
        }
        if(tab == "store") {
            Button(onClick = { editingId = null; adding = true }, modifier = Modifier.fillMaxWidth()) { Text("افزودن فروشگاه / نمایشگاه") }
            Text("کالاها و موجودی، کاتالوگ مشترک کسب‌وکار هستند. فاکتور و گزارش هر فروشگاه جداست.", style = MaterialTheme.typography.bodySmall)
            if(state.data.stores.isEmpty()) Text("اولین فروشگاه یا نمایشگاه را اضافه کنید.")
            state.data.stores.forEach { store -> key(store.id) {
                StoreSwipeCard(store.name, listOfNotNull(store.brand.takeIf { it.isNotBlank() }?.let { "برند: " + it },store.phone?.let { "تلفن: " + it },store.address?.let { "آدرس: " + it },store.startsOn?.let { "زمان: " + dateLabel(it) + " تا " + dateLabel(store.endsOn.orEmpty()) }).joinToString("\n"),
                    { editingId = store.id; adding = true }, { deleting = store.id to store.name }, store.logoPath,
                    selected = state.data.store?.id == store.id, select = { vm.selectStore(store.id) })
            } }
        } else if(tab == "brand") {
            Button(onClick = { editingBrand = null; adding = true }, modifier = Modifier.fillMaxWidth()) { Text("افزودن برند") }
            if(state.data.brands.isEmpty()) Text("برند و نشان آن را تعریف کنید، سپس در فرم فروشگاه انتخاب کنید.")
            state.data.brands.forEach { brand -> key(brand.id) {
                StoreSwipeCard(brand.name, "هویت بصری برند", { editingBrand = brand; adding = true }, { deletingBrand = brand }, brand.image)
            } }
        } else {
            Button(onClick = { editingId = null; adding = true }) { Text("افزودن") }
            when(tab) {
                "seller" -> {
                    if(state.data.sellers.isEmpty()) Text("هنوز فروشنده‌ای ثبت نشده است.")
                    state.data.sellers.forEach { value ->
                        key(value.id) { StoreSwipeCard(value.fullName, value.phone, { editingId = value.id; adding = true }, { deleting = value.id to value.fullName }, value.signaturePath) }
                    }
                }
                "product" -> {
                    if(state.data.products.isEmpty()) Text("هنوز کالایی ثبت نشده است.")
                    state.data.products.forEach { value ->
                        key(value.id) { StoreSwipeCard(value.name, "موجودی: " + value.stock + " | " + money(value.price) + value.color?.let { " · رنگ: " + it }.orEmpty(), { editingId = value.id; adding = true }, { deleting = value.id to value.name }, value.image) }
                    }
                }
                "material" -> {
                    if(state.data.materials.isEmpty()) Text("هنوز ماده اولیه‌ای ثبت نشده است.")
                    state.data.materials.forEach { value ->
                        key(value.id) { StoreSwipeCard(value.name, "موجودی: " + quantity(value.quantity) + " " + value.unit + "\n" + money(value.pricePerUnit) + "\n" + value.purchaseDate.orEmpty().let(::dateLabel),
                            { editingId = value.id; adding = true }, { deleting = value.id to value.name }) }
                    }
                }
            }
        }
    }
    if(adding) key(tab, editing) {
        val dismiss = { adding = false; vm.clearError() }
        val save: (Any) -> Unit = { value -> vm.save(value) { adding = false } }
        when(tab) {
            "store" -> CatalogEditor("store", editing as? Store, state.busy, state.error, save, dismiss, state.data.brands, state.data.finance)
            "brand" -> BrandEditor(editingBrand, state.busy, state.error, { save(it) }, dismiss)
            "seller" -> ir.siamak.fintrack.store.presentation.baseinfo.seller.SellerManagementScreen(editing as? Seller,state.busy,state.error,{ save(it) },dismiss)
            "product" -> ir.siamak.fintrack.store.presentation.baseinfo.product.ProductManagementScreen(editing as? Product,state.busy,state.error,{ save(it) },dismiss)
            else -> ir.siamak.fintrack.store.presentation.baseinfo.material.AddMaterialScreen(editing as? RawMaterial,state.busy,state.error,{ save(it) },dismiss)
        }
    }
    deleting?.let { target -> ConfirmDelete(target.second, { vm.delete(tab, target.first) { deleting = null } }, { deleting = null; vm.clearError() }, state.error, state.busy) }
    deletingBrand?.let { brand -> ConfirmDelete(brand.name, { vm.deleteBrand(brand.id) { deletingBrand = null } }, { deletingBrand = null; vm.clearError() }, state.error, state.busy) }
}
@Composable internal fun CatalogEditor(kind: String, original: Any?, busy: Boolean, remoteError: String?, save: (Any) -> Unit, dismiss: () -> Unit, brands: List<Brand> = emptyList(), finance: StoreFinanceDirectory = StoreFinanceDirectory()) {
    val seed = when(original) {
        is Store -> listOf(original.name, original.brand, original.phone.orEmpty(), original.address.orEmpty(), original.description.orEmpty(), original.logoPath.orEmpty())
        is Seller -> listOf(original.firstName, original.lastName, original.phone, original.address.orEmpty(), original.description.orEmpty(), original.signaturePath.orEmpty(), original.cardNumber.orEmpty(), original.accountNumber.orEmpty())
        is Product -> listOf(original.name, original.material.orEmpty(), original.size.orEmpty(), original.weight.orEmpty(), original.stock.toString(), original.price.toString(), original.image.orEmpty(), original.color.orEmpty())
        is RawMaterial -> listOf(original.name, original.unit, original.pricePerUnit.toString(), original.quantity.toString(), original.purchaseDate.orEmpty(), original.shippingCost?.toString().orEmpty())
        else -> when(kind) { "store" -> List(6) { "" }; "seller" -> List(8) { "" }; "product" -> listOf("","","","","0","0","",""); else -> listOf("","","","",java.time.LocalDate.now().toString(),"") }
    }
    var fields by rememberSaveable { mutableStateOf(seed) }
    var selectedBrandId by rememberSaveable { mutableStateOf((original as? Store)?.brandId) }
    var exhibition by rememberSaveable { mutableStateOf((original as? Store)?.startsOn != null) }
    var startsOn by rememberSaveable { mutableStateOf((original as? Store)?.startsOn ?: java.time.LocalDate.now().toString()) }
    var endsOn by rememberSaveable { mutableStateOf((original as? Store)?.endsOn ?: java.time.LocalDate.now().toString()) }
    var walletId by rememberSaveable { mutableStateOf((original as? Store)?.settlement?.walletId) }
    var memberId by rememberSaveable { mutableStateOf((original as? Store)?.settlement?.memberId) }
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
                    if(kind == "store" && index == 1) {
                        ChoiceEntry("برند (اختیاری)", fields[1].ifBlank { "بدون برند" }, listOf<String?>(null).map { it to "بدون برند" } + brands.map { it.id to it.name }) { id ->
                            selectedBrandId = id; val brand = brands.firstOrNull { it.id == id }; update(1,brand?.name.orEmpty()); update(5,brand?.image.orEmpty())
                        }
                    }
                    else if(kind == "material" && index == 4) { DateEntry(label, fields[index]) { update(index,it) }; TextButton(onClick = { update(index,"") }) { Text("بدون تاریخ خرید") } }
                    else Entry(label, fields[index], { if(kind == "store" && index == 1) selectedBrandId = null; update(index,it) }, number = (kind == "product" && index in 4..5) || (kind == "material" && index in listOf(2,3,5)))
                }
                if(kind == "store") {
                    FormSection("زمان نمایشگاه") {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(exhibition, { exhibition = it }); Text("نمایشگاه با تاریخ شروع و پایان") }
                        if(exhibition) { DateEntry("شروع", startsOn) { startsOn = it }; DateEntry("پایان / تمدید", endsOn) { endsOn = it } }
                    }
                    FormSection("کیف پول دریافت‌ها") {
                        ChoiceEntry("کیف پول شخصی", finance.wallets.firstOrNull { it.id == walletId }?.name ?: "بدون اتصال", listOf(null to "بدون اتصال") + finance.wallets.map { it.id to it.name }) { walletId = it; memberId = null }
                        val wallet = finance.wallets.firstOrNull { it.id == walletId }
                        if(wallet != null) ChoiceEntry("شخص دریافت‌کننده", finance.members.firstOrNull { it.id == memberId }?.name ?: "انتخاب شخص", finance.members.filter { it.contextId == wallet.contextId }.map { it.id to it.name }) { memberId = it }
                    }
                }
                if(kind == "product") Entry("رنگ (اختیاری)", fields[7], { update(7,it) })
                if(kind == "store" || kind == "product") {
                    val index = if(kind == "store") 5 else 6
                    if(kind != "store" || selectedBrandId == null) ImageEntry(if(kind == "store") "انتخاب نشان فروشگاه" else "انتخاب تصویر کالا", fields[index].ifBlank { null }) { update(index,it.orEmpty()) }
                }
                if(kind == "seller") {
                    ImageEntry("انتخاب تصویر امضای فروشنده", fields[5].ifBlank { null }) { update(5,it.orEmpty()) }
                    Entry("شماره کارت (اختیاری)", fields[6], { update(6,it) })
                    Entry("شماره حساب (اختیاری)", fields[7], { update(7,it) })
                }
                Button(enabled = !busy, onClick = {
                    try {
                        fun optional(index: Int) = fields[index].trim().ifBlank { null }
                        val value: Any = when(kind) {
                            "store" -> {
                                val account = walletId?.let { id ->
                                    val wallet = requireNotNull(finance.wallets.firstOrNull { it.id == id }) { "کیف پول انتخاب‌شده در دسترس نیست" }
                                    StoreSettlementAccount(wallet.contextId,id,requireNotNull(memberId) { "شخص دریافت‌کننده را انتخاب کنید" })
                                }
                                Store((original as? Store)?.id ?: 0, fields[0].trim(), fields[1].trim(), optional(5), optional(2), optional(3), optional(4), selectedBrandId,
                                    if(exhibition) startsOn else null, if(exhibition) endsOn else null, account)
                            }
                            "seller" -> Seller((original as? Seller)?.id ?: 0, fields[0].trim(), fields[1].trim(), fields[2].trim(), optional(3), optional(4), optional(5), optional(6), optional(7))
                            "product" -> {
                                val count = whole(fields[4], true); require(count in 0..Int.MAX_VALUE.toLong())
                                Product((original as? Product)?.id ?: 0, fields[0].trim(), optional(1), optional(2), optional(3), count.toInt(), whole(fields[5], true), optional(6), optional(7))
                            }
                            else -> RawMaterial((original as? RawMaterial)?.id ?: 0, fields[0].trim(), fields[1].trim(), whole(fields[2]), decimal(fields[3]), optional(4), optional(5)?.let { whole(it) })
                        }
                        save(value)
                    } catch(e: Exception) { error = e.message ?: "اطلاعات نامعتبر است" }
                }, modifier = Modifier.fillMaxWidth()) { Text(if(busy) "در حال ثبت…" else "ذخیره") }
                FilledTonalButton(enabled = !busy, onClick = dismiss, modifier = Modifier.fillMaxWidth()) { Text("انصراف") }
            }
        }
    }
}

@Composable private fun BrandEditor(original: Brand?, busy: Boolean, error: String?, save: (Brand) -> Unit, dismiss: () -> Unit) {
    var name by rememberSaveable(original?.id) { mutableStateOf(original?.name.orEmpty()) }
    var image by rememberSaveable(original?.id) { mutableStateOf(original?.image) }
    val id = rememberSaveable(original?.id) { original?.id ?: java.util.UUID.randomUUID().toString() }
    AlertDialog(onDismissRequest = { if(!busy) dismiss() }, title = { Text(if(original == null) "برند جدید" else "ویرایش برند") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Entry("نام برند", name, { name = it }); ImageEntry("انتخاب تصویر برند", image, { image = it })
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        } }, confirmButton = { TextButton(enabled = !busy && name.isNotBlank(), onClick = { save(Brand(id, name.trim(), image)) }) { Text("ذخیره") } },
        dismissButton = { TextButton(enabled = !busy, onClick = dismiss) { Text("انصراف") } })
}
