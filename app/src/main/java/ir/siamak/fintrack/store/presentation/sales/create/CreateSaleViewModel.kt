package ir.siamak.fintrack.store.presentation.sales.create
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.sale.CreateSaleUseCase
import ir.siamak.fintrack.store.domain.usecase.store.ObserveStoreWorkspaceUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import java.time.LocalDate
import java.util.UUID

@Serializable data class SaleDraft(
    val name: String = "", val phone: String = "", val address: String = "",
    val sellerId: Long? = null, val quantities: Map<Long,Int> = emptyMap(),
    val discount: String = "0", val percentDiscount: Boolean = false, val tax: String = "0",
    val installments: Boolean = false, val months: String = "3", val monthlyPercent: String = "5",
    val firstDueDate: String = LocalDate.now().plusMonths(1).toString(), val step: Int = 0,
    val requestId: String = UUID.randomUUID().toString()
) {
    fun request() = SaleRequest(Customer(name = name.trim(), phone = phone.trim(), address = address.ifBlank { null }), sellerId,
        quantities, Discount(if(percentDiscount) Discount.DiscountType.PERCENT else Discount.DiscountType.FIXED, number(discount)),
        number(tax).toIntChecked(), if(installments) number(months).toIntChecked() else 0, if(installments) number(monthlyPercent).toIntChecked() else 0, firstDueDate, requestId)
}
internal fun number(text: String): Long = ir.siamak.fintrack.store.presentation.components.whole(text)
internal fun Long.toIntChecked(): Int { require(this in 0..Int.MAX_VALUE.toLong()) { "عدد خارج از محدوده است" }; return toInt() }
@HiltViewModel class CreateSaleViewModel @Inject constructor(
    private val saved: SavedStateHandle, observe: ObserveStoreWorkspaceUseCase, private val checkout: CreateSaleUseCase
) : ViewModel() {
    private val mutable = MutableStateFlow(CreateSaleUiState(draft = saved.get<String>("draft")?.let { runCatching { Json.decodeFromString<SaleDraft>(it) }.getOrNull() } ?: SaleDraft()))
    val state = mutable.asStateFlow()
    init {
        viewModelScope.launch {
            observe().catch { e -> mutable.update { it.copy(loading = false, error = e.message) } }.collect { workspace ->
                mutable.update { it.copy(workspace = workspace, loading = false) }
                workspace.invoices.firstOrNull { it.requestId == mutable.value.draft.requestId }?.let { invoice ->
                    saved.remove<String>("draft")
                    mutable.update { it.copy(invoiceId = invoice.id) }
                }
                refresh()
            }
        }
    }
    fun onEvent(event: CreateSaleEvent) { when(event) {
        is CreateSaleEvent.Edit -> edit(event.change)
        CreateSaleEvent.Next -> next()
        CreateSaleEvent.Back -> back()
        CreateSaleEvent.Submit -> submit()
    } }
    fun edit(change: (SaleDraft) -> SaleDraft) {
        if(mutable.value.busy || mutable.value.invoiceId != null) return
        val draft = change(mutable.value.draft)
        saved["draft"] = Json.encodeToString(draft)
        mutable.update { it.copy(draft = draft, error = null) }
        refresh()
    }
    fun next() {
        val current = mutable.value
        val error = when(current.draft.step) {
            0 -> if(current.draft.name.isBlank() || current.draft.phone.isBlank()) "نام و تلفن مشتری الزامی است" else null
            else -> runCatching { checkout.preview(current.draft.request(), current.workspace) }.exceptionOrNull()?.message
        }
        if(error != null) mutable.update { it.copy(error = error) }
        else edit { it.copy(step = (it.step + 1).coerceAtMost(4)) }
    }
    fun back() = edit { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    private fun refresh() {
        val current = mutable.value
        val result = runCatching { checkout.preview(current.draft.request(), current.workspace) }
        mutable.update { it.copy(preview = result.getOrNull()) }
    }
    fun submit() {
        if(mutable.value.busy || mutable.value.invoiceId != null) return
        viewModelScope.launch {
            mutable.update { it.copy(busy = true, error = null) }
            try {
                val current = mutable.value
                val id = checkout(current.draft.request(), current.workspace)
                saved.remove<String>("draft")
                mutable.update { it.copy(invoiceId = id) }
            } catch(cancel: CancellationException) { throw cancel }
            catch(e: Exception) { mutable.update { it.copy(error = e.message ?: "ثبت فروش ناموفق بود") } }
            finally { mutable.update { it.copy(busy = false) } }
        }
    }
}
