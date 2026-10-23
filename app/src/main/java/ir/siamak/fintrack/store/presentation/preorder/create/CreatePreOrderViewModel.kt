package ir.siamak.fintrack.store.presentation.preorder.create
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.preorder.CreatePreOrderUseCase
import ir.siamak.fintrack.store.domain.usecase.store.ObserveStoreWorkspaceUseCase
import ir.siamak.fintrack.store.presentation.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import java.time.LocalDate
import java.util.UUID

@Serializable data class PreOrderDraft(
    val name: String = "", val phone: String = "", val address: String = "", val description: String = "",
    val deliveryDate: String = LocalDate.now().plusWeeks(1).toString(), val quantities: Map<Long,String> = emptyMap(),
    val productionCost: String = "0", val profit: String = "0", val shippingCost: String = "0", val agreedPrice: String = "0",
    val advance: String = "0", val advanceIsPercent: Boolean = false, val step: Int = 0,
    val requestId: String = UUID.randomUUID().toString()
) {
    fun request() = PreOrderRequest(Customer(name = name.trim(), phone = phone.trim(), address = address.ifBlank { null }), description.trim(), deliveryDate,
        quantities.filterValues { it.isNotBlank() && decimal(it) != 0.0 }.mapValues { decimal(it.value) },
        whole(productionCost), whole(profit), whole(shippingCost), whole(agreedPrice), whole(advance), advanceIsPercent, requestId)
}
@HiltViewModel class CreatePreOrderViewModel @Inject constructor(
    private val saved: SavedStateHandle, observe: ObserveStoreWorkspaceUseCase, private val place: CreatePreOrderUseCase
) : ViewModel() {
    private val mutable = MutableStateFlow(CreatePreOrderUiState(draft = saved.get<String>("draft")?.let { runCatching { Json.decodeFromString<PreOrderDraft>(it) }.getOrNull() } ?: PreOrderDraft()))
    val state = mutable.asStateFlow()
    init { viewModelScope.launch {
        observe().catch { e -> mutable.update { it.copy(loading = false, error = e.message) } }.collect { workspace ->
            mutable.update { it.copy(workspace = workspace, loading = false) }
                workspace.invoices.firstOrNull { it.requestId == mutable.value.draft.requestId }?.let { invoice ->
                    saved.remove<String>("draft")
                    mutable.update { it.copy(invoiceId = invoice.id) }
                }
                refresh()
        }
    } }
    fun onEvent(event: CreatePreOrderEvent) { when(event) {
        is CreatePreOrderEvent.Edit -> edit(event.change)
        CreatePreOrderEvent.Next -> next()
        CreatePreOrderEvent.Back -> back()
        CreatePreOrderEvent.Submit -> submit()
    } }
    fun edit(change: (PreOrderDraft) -> PreOrderDraft) {
        if(mutable.value.busy || mutable.value.invoiceId != null) return
        val draft = change(mutable.value.draft)
        saved["draft"] = Json.encodeToString(draft)
        mutable.update { it.copy(draft = draft, error = null) }; refresh()
    }
    fun next() {
        val current = mutable.value
        val error = when(current.draft.step) {
            0 -> if(current.draft.name.isBlank() || current.draft.phone.isBlank()) "نام و تلفن مشتری الزامی است" else null
            1 -> if(current.draft.description.isBlank()) "شرح سفارش الزامی است" else null
            2 -> runCatching { current.draft.request(); Unit }.exceptionOrNull()?.message
            else -> runCatching { place.preview(current.draft.request(), current.workspace) }.exceptionOrNull()?.message
        }
        if(error != null) mutable.update { it.copy(error = error) }
        else edit { it.copy(step = (it.step + 1).coerceAtMost(5)) }
    }
    fun back() = edit { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    private fun refresh() {
        val current = mutable.value
        val request = runCatching { current.draft.request() }.getOrNull()
        val preview = request?.let { runCatching { place.preview(it,current.workspace) }.getOrNull() }
        val suggested = request?.let { runCatching { place.suggested(it,current.workspace) }.getOrNull() }
        mutable.update { it.copy(preview = preview, suggested = suggested, materialCost = request?.let { value -> runCatching { place.materialCost(value,current.workspace) }.getOrNull() }) }
    }
    fun submit() {
        if(mutable.value.busy || mutable.value.invoiceId != null) return
        viewModelScope.launch {
            mutable.update { it.copy(busy = true, error = null) }
            try {
                val current = mutable.value
                val id = place(current.draft.request(), current.workspace)
                saved.remove<String>("draft")
                mutable.update { it.copy(invoiceId = id) }
            } catch(cancel: CancellationException) { throw cancel }
            catch(e: Exception) { mutable.update { it.copy(error = e.message ?: "ثبت سفارش ناموفق بود") } }
            finally { mutable.update { it.copy(busy = false) } }
        }
    }
}
