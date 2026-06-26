package ir.siamak.fintrack.presentation.wallet.add_edit_wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.usecase.wallet.GetWalletByIdUseCase
import ir.siamak.fintrack.domain.usecase.wallet.InsertWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.UpdateWalletUseCase
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
            is AddEditWalletEvent.EnteredName -> {
                _state.update { it.copy(name = event.value) }
            }

            is AddEditWalletEvent.EnteredBalance -> {
                _state.update { it.copy(balance = event.value) }
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
            val balance = _state.value.balance.toDoubleOrNull() ?: 0.0
            val color = _state.value.color

            if (name.isBlank()) return@launch

            _state.update { it.copy(isLoading = true) }

            val wallet = Wallet(
                id = currentWalletId ?: 0L,
                name = name,
                balance = balance,
                color = color
            )

            if (currentWalletId == null) {
                insertWalletUseCase(wallet)
            } else {
                updateWalletUseCase(wallet)
            }

            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }


    private fun loadWallet(walletId: Long) {
        viewModelScope.launch {
            currentWalletId = walletId

            val wallet = getWalletByIdUseCase(walletId)

            wallet?.let {
                _state.update { state ->
                    state.copy(
                        name = it.name,
                        balance = it.balance.toString(),
                        color = it.color
                    )
                }
            }
        }
    }

}
