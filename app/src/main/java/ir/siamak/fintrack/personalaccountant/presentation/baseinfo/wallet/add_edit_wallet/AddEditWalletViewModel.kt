package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.wallet.add_edit_wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.GetWalletByIdUseCase
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.InsertWalletUseCase
import ir.siamak.fintrack.personalaccountant.domain.usecase.wallet.UpdateWalletUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ویومدل مربوط به صفحه افزودن/ویرایش کیف ‌پول.
 *
 * این کلاس ورودی‌های کاربر را مدیریت می‌کند و پس از اعتبارسنجی اولیه،
 * داده‌ها را از طریق [InsertWalletUseCase] در دیتابیس ذخیره می‌کند.
 */
@HiltViewModel
class AddEditWalletViewModel @Inject constructor(
    private val insertWalletUseCase: InsertWalletUseCase,
    private val updateWalletUseCase: UpdateWalletUseCase,
    private val getWalletByIdUseCase: GetWalletByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditWalletState())
    val state = _state.asStateFlow()

    private var currentWalletId: Long? = null

    /**
     * مدیریت رویدادهای دریافتی از رابط کاربری.
     *
     * @param event رویدادی که توسط کاربر در صفحه ایجاد شده است.
     */
    fun onEvent(event: AddEditWalletEvent) {
        when (event) {
            is AddEditWalletEvent.CardChanged -> _state.update { it.copy(cardNumber = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(event.value).filter(Char::isDigit).take(16)) }
            is AddEditWalletEvent.AccountChanged -> _state.update { it.copy(accountNumber = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(event.value).filter { it.isDigit() || it in "-." }.take(40)) }
            is AddEditWalletEvent.EnteredName -> {
                _state.update { it.copy(name = event.value) }
            }

            is AddEditWalletEvent.EnteredBalance -> {
                val clean = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(event.value).replace(",", "")
                if(Regex("-?[0-9]*(?:\\.[0-9]*)?").matches(clean)) _state.update { it.copy(balanceRaw = clean, balance = clean) }
            }

            is AddEditWalletEvent.LoadWallet -> {
                loadWallet(event.walletId)
            }

            is AddEditWalletEvent.EnteredColor -> {
                _state.update { it.copy(color = event.value) }
            }

            AddEditWalletEvent.SaveWallet -> {
                saveWallet()
            }
        }
    }


    /**
     * اطلاعات وارد شده را به مدل [Wallet] تبدیل کرده و در دیتابیس ذخیره می‌کند.
     *
     * در این نسخه، برای هر کیف ‌پول یک رنگ ثابت اولیه در نظر گرفته شده است
     * تا خطای مربوط به نبود پارامتر `color` برطرف شود.
     */
    private fun saveWallet() {
        viewModelScope.launch {
            val name = _state.value.name.trim()
            val balance = _state.value.balanceRaw.toDoubleOrNull() ?: 0.0
            val color = _state.value.color

            if (name.isBlank()) return@launch
            if(!balance.isFinite()) { _state.update { it.copy(error = "موجودی معتبر نیست") }; return@launch }
            if(_state.value.cardNumber.isNotBlank() && _state.value.cardNumber.length != 16) { _state.update { it.copy(error = "شماره کارت باید ۱۶ رقم باشد") }; return@launch }

            _state.update { it.copy(isLoading = true) }

            val wallet = Wallet(
                id = currentWalletId ?: 0L,
                name = name,
                balance = balance,
                color = color, cardNumber = _state.value.cardNumber.ifBlank { null }, accountNumber = _state.value.accountNumber.ifBlank { null }, isCash = _state.value.isCash
            )

            try {
            if (currentWalletId == null) {
                insertWalletUseCase(wallet)
            } else {
                updateWalletUseCase(wallet)
            }

            _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch(e: Exception) { _state.update { it.copy(isLoading = false, error = e.message ?: "ذخیره ناموفق بود") } }
        }
    }


    private fun loadWallet(walletId: Long) {
        viewModelScope.launch {
            currentWalletId = walletId

            val wallet = getWalletByIdUseCase(walletId)

            wallet?.let {
                _state.update { state ->
                    state.copy(
                        name = it.name, cardNumber = it.cardNumber.orEmpty(), accountNumber = it.accountNumber.orEmpty(), isCash = it.isCash,
                        balance = java.math.BigDecimal.valueOf(it.balance).stripTrailingZeros().toPlainString(),
                        balanceRaw = java.math.BigDecimal.valueOf(it.balance).stripTrailingZeros().toPlainString(),
                        color = it.color
                    )
                }
            }
        }
    }

}

