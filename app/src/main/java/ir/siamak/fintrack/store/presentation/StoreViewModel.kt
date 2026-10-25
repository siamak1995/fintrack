package ir.siamak.fintrack.store.presentation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.usecase.store.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class StoreFeatureViewModel(
    private val observe: ObserveStoreWorkspaceUseCase,
    private val commands: StoreCommandsUseCase
) : ViewModel() {
    private val mutable = MutableStateFlow(StoreUiState())
    val state = mutable.asStateFlow()
    private var observer: kotlinx.coroutines.Job? = null
    init { reload() }
    fun reload() {
        observer?.cancel()
        observer = viewModelScope.launch {
            mutable.update { it.copy(loading = true, error = null) }
            observe().map { data -> data to CalculateStoreOverviewUseCase()(data) }.catch { error -> mutable.update { it.copy(loading = false, error = error.message ?: "خواندن اطلاعات ناموفق بود") } }
                .collect { (data, overview) -> mutable.update { it.copy(loading = false, data = data, overview = overview) } }
        }
    }
    fun clearError() { mutable.update { it.copy(error = null) } }
    fun save(value: Any, done: () -> Unit) = execute(done) {
        when(value) {
            is Store -> commands.saveStore(value)
            is Seller -> commands.saveSeller(value)
            is Product -> commands.saveProduct(value)
            is RawMaterial -> commands.saveMaterial(value)
            is Brand -> commands.saveBrand(value)
            else -> error("نوع اطلاعات نامعتبر است")
        }
    }
    fun delete(kind: String, id: Long, done: () -> Unit) = execute(done) {
        when(kind) {
            "product" -> commands.deleteProduct(id)
            "seller" -> commands.deleteSeller(id)
            "material" -> commands.deleteMaterial(id)
            "store" -> commands.deleteStore(id)
            else -> error("حذف مجاز نیست")
        }
    }
    fun selectStore(id: Long) = execute { commands.selectStore(id) }
    fun deleteBrand(id: String, done: () -> Unit) = execute(done) { commands.deleteBrand(id) }
    fun status(id: Long, value: PreOrderStatus) = execute { commands.changeStatus(id, value) }
    fun receive(id: Long, amount: Long, done: () -> Unit) = execute(done) { commands.receivePayment(id, amount) }
    private fun execute(done: () -> Unit = {}, block: suspend () -> Unit) {
        if(mutable.value.busy) return
        viewModelScope.launch {
            mutable.update { it.copy(busy = true, error = null) }
            try { block(); done() }
            catch (cancel: CancellationException) { throw cancel }
            catch (e: Exception) { mutable.update { it.copy(error = e.message ?: "ثبت اطلاعات ناموفق بود") } }
            finally { mutable.update { it.copy(busy = false) } }
        }
    }
}

@HiltViewModel
class StoreViewModel @Inject constructor(observe: ObserveStoreWorkspaceUseCase, commands: StoreCommandsUseCase) : StoreFeatureViewModel(observe, commands)
