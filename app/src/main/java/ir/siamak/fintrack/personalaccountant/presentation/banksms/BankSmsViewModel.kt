package ir.siamak.fintrack.personalaccountant.presentation.banksms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.*
import ir.siamak.fintrack.personalaccountant.banksms.*
import ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.math.BigDecimal

data class BankSmsUiState(val loading: Boolean = true, val inbox: BankSmsInboxState = BankSmsInboxState(),
    val wallets: List<Wallet> = emptyList(), val members: List<Member> = emptyList(), val busy: Boolean = false, val error: String? = null, val tags: List<Tag> = emptyList(), val loans: List<Installment> = emptyList())
private data class SmsActionState(val busy: Boolean = false, val error: String? = null)

@HiltViewModel class BankSmsViewModel @Inject constructor(private val inbox: BankSmsInbox,
    @PersonalAccountingDatabase db: AppDatabase, private val importSms: ImportBankSms) : ViewModel() {
    private val action = MutableStateFlow(SmsActionState())
    private val directory = combine(db.walletDao().getAllWallets().onStart { db.walletDao().ensureCash() },
        db.memberDao().getAllMembers(),db.tagDao().getAllTags(),db.installmentDao().getAllInstallments()) { wallets,members,tags,loans ->
        BankSmsUiState(wallets = wallets.filter { !it.isDeleted && it.contextId == 1L }.map { it.toModel() },
            members = members.filter { !it.isDeleted && it.contextId == 1L }.map { it.toModel() },
            tags = tags.filter { !it.isDeleted && it.contextId == 1L }.map { it.toModel() },loans = loans.map { it.toModel() })
    }
    val state = combine(inbox.state,directory,action) { review,rows,event ->
        rows.copy(loading = false,inbox = review,busy = event.busy,error = event.error)
    }.catch { emit(BankSmsUiState(loading = false,error = it.message)) }
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),BankSmsUiState())
    fun enable(value: Boolean) = execute { inbox.setEnabled(value) }
    fun paste(body: String, done: () -> Unit) = execute {
        require(inbox.capture(body.trim(),"ورود دستی",System.currentTimeMillis(),automatic = false)) { "پیام قابل تشخیص نیست، قبلاً بررسی شده یا در فهرست انتظار است. متن کامل پیامک بانکی را وارد کنید." }
        done()
    }
    fun discard(id: String, blockSender: Boolean = false, done: () -> Unit = {}) = execute { inbox.discard(id,blockSender); done() }
    fun unblock(sender: String) = execute { inbox.unblock(sender) }
    fun approve(candidate: BankSmsCandidate, amount: String, currency: SmsCurrency?, kind: BankSmsKind?, walletId: Long?, toWalletId: Long?, memberId: Long?, balance: String?, balanceCurrency: SmsCurrency?, newWalletName: String?, balanceOnTarget: Boolean, tagIds: List<Long>, loanId: Long?, done: () -> Unit) = execute {
        val operation = requireNotNull(kind) { "نوع تراکنش را انتخاب کنید" }
        val unit = requireNotNull(currency) { "واحد مبلغ پیامک را انتخاب کنید" }
        val raw = PersianNumberFormatter.normalize(amount).replace(",", "").toBigDecimalOrNull() ?: error("مبلغ نامعتبر است")
        val toman = if(unit == SmsCurrency.RIAL) raw.divide(BigDecimal.TEN) else raw
        val snapshot = balance?.let {
            val rawBalance = PersianNumberFormatter.normalize(it).replace(",", "").toBigDecimalOrNull() ?: error("مانده نامعتبر است")
            val balanceUnit = requireNotNull(balanceCurrency) { "واحد مانده را انتخاب کنید" }
            if(balanceUnit == SmsCurrency.RIAL) rawBalance.divide(BigDecimal.TEN) else rawBalance
        }
        importSms(candidate,toman,operation,requireNotNull(walletId) { "حساب را انتخاب کنید" },toWalletId,requireNotNull(memberId) { "شخص را انتخاب کنید" },snapshot,newWalletName,balanceOnTarget,tagIds,loanId)
        done()
    }
    fun clearError() { action.update { it.copy(error = null) } }
    private fun execute(block: suspend () -> Unit) {
        if(action.value.busy) return
        action.value = SmsActionState(busy = true)
        viewModelScope.launch {
            try { block() }
            catch(cancel: CancellationException) { throw cancel }
            catch(e: Exception) { action.value = SmsActionState(error = e.message ?: "عملیات ناموفق بود") }
            finally { action.update { it.copy(busy = false) } }
        }
    }
}
