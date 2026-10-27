package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.installment.add_edit_installment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.usecase.installments.InstallmentUseCases
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.WalletUseCases
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditInstallmentsViewModel @Inject constructor(private val installmentUseCases: InstallmentUseCases,
    private val walletUseCases: WalletUseCases) : ViewModel() {
    private val mutable = MutableStateFlow(AddEditInstallmentState())
    val state = mutable.asStateFlow()
    private var original: Installment? = null
    private var loadedId: Long? = null
    init { viewModelScope.launch { walletUseCases.getAllWallets().collect { rows ->
        mutable.update { it.copy(wallets = rows, walletId = if(it.walletId == 0L) rows.firstOrNull()?.id ?: 0L else it.walletId) }
    } } }
    fun edit(change: (AddEditInstallmentState) -> AddEditInstallmentState) {
        if(!mutable.value.isLoading) mutable.update { change(it).copy(error = null) }
    }
    fun load(id: Long?) {
        if(id == null || loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            mutable.update { it.copy(isLoading = true) }
            try {
                val row = requireNotNull(installmentUseCases.getInstallmentById(id)) { "وام پیدا نشد" }
                original = row
                mutable.update { it.copy(title = row.title, totalAmountRaw = amount(row.totalAmount), paidAmountRaw = amount(row.paidAmount),
                    dueDate = row.dueDate, startDate = row.startDate, walletId = row.walletId, note = row.note.orEmpty(),
                    installmentCountRaw = row.installmentCount.toString(), automaticPayment = row.automaticPayment, isLoading = false) }
            } catch(e: Exception) { mutable.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
    fun onEvent(event: AddEditInstallmentEvent) { when(event) {
        is AddEditInstallmentEvent.TitleChanged -> edit { it.copy(title = event.value) }
        is AddEditInstallmentEvent.NoteChanged -> edit { it.copy(note = event.note) }
        is AddEditInstallmentEvent.WalletChanged -> edit { it.copy(walletId = event.walletId) }
        is AddEditInstallmentEvent.DueDateChanged -> edit { it.copy(dueDate = event.value) }
        is AddEditInstallmentEvent.TotalAmountChanged -> edit { it.copy(totalAmountRaw = clean(event.value)) }
        is AddEditInstallmentEvent.PaidAmountChanged -> edit { it.copy(paidAmountRaw = clean(event.value)) }
        AddEditInstallmentEvent.Save -> save()
    } }
    private fun save() {
        if(mutable.value.isLoading) return
        viewModelScope.launch {
            mutable.update { it.copy(isLoading = true, error = null) }
            try {
                val s = mutable.value
                require(s.title.isNotBlank()) { "عنوان وام یا قسط را وارد کنید" }
                val total = s.totalAmountRaw.toDoubleOrNull() ?: error("مبلغ کل نامعتبر است")
                val paid = s.paidAmountRaw.ifBlank { "0" }.toDoubleOrNull() ?: error("مبلغ پرداختی نامعتبر است")
                val count = s.installmentCountRaw.toIntOrNull() ?: error("تعداد اقساط نامعتبر است")
                require(total.isFinite() && total > 0 && total <= 9007199254740991.0 && paid.isFinite() && paid in 0.0..total) { "مبلغ کل و پرداخت‌شده را بررسی کنید" }
                require(count in 1..600 && total >= count) { "تعداد اقساط باید بین یک و ۶۰۰ و متناسب با مبلغ باشد" }
                val zone = java.time.ZoneId.systemDefault()
                val startDay = java.time.Instant.ofEpochMilli(s.startDate).atZone(zone).toLocalDate()
                val dueDay = java.time.Instant.ofEpochMilli(s.dueDate).atZone(zone).toLocalDate()
                require(!startDay.isAfter(dueDay)) { "شروع وام نباید بعد از اولین سررسید باشد" }
                require(s.wallets.any { it.id == s.walletId }) { "حساب پرداخت را انتخاب کنید" }
                val row = Installment(id = original?.id ?: 0, contextId = original?.contextId ?: 1,
                    title = s.title.trim(), totalAmount = total, paidAmount = paid, dueDate = dueDay.atStartOfDay(zone).toInstant().toEpochMilli(),
                    createdAt = original?.createdAt ?: System.currentTimeMillis(), note = s.note.ifBlank { null },
                    walletId = s.walletId, isPaid = paid >= total, installmentCount = count, startDate = startDay.atStartOfDay(zone).toInstant().toEpochMilli(), automaticPayment = s.automaticPayment)
                if(original == null) installmentUseCases.insertInstallment(row) else installmentUseCases.updateInstallment(row)
                mutable.update { it.copy(isLoading = false, isSaved = true) }
            } catch(cancel: kotlinx.coroutines.CancellationException) { throw cancel }
            catch(e: Exception) { mutable.update { it.copy(isLoading = false, error = e.message ?: "ثبت ناموفق بود") } }
        }
    }
    private fun clean(value: String) = PersianNumberFormatter.normalize(value).replace(",", "").filter { it.isDigit() || it == '.' }
    private fun amount(value: Double) = java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
}
