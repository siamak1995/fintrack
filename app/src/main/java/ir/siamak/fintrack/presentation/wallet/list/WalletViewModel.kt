package ir.siamak.fintrack.presentation.wallet.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ویومدل صفحه لیست حساب‌ها.
 *
 * این ViewModel مسئول دریافت و مدیریت داده‌های موردنیاز صفحه حساب‌ها است.
 * در لایه data/domain هنوز از مفهوم Wallet استفاده می‌شود، اما در UI این بخش
 * با عنوان «حساب» یا «حساب بانکی» نمایش داده می‌شود.
 *
 * وظایف اصلی این ویومدل:
 * - دریافت لیست حساب‌ها از domain layer
 * - مدیریت loading / success / error state
 * - حذف حساب انتخاب‌شده
 */
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletUseCases: WalletUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WalletUiState(isLoading = true)
    )
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWallets()
    }

    /**
     * لیست حساب‌ها را از منبع داده دریافت می‌کند و state صفحه را به‌روزرسانی می‌کند.
     *
     * در صورت موفقیت:
     * - wallets مقداردهی می‌شود
     * - loading متوقف می‌شود
     *
     * در صورت خطا:
     * - پیام خطا در state ذخیره می‌شود
     */
    private fun loadWallets() {
        viewModelScope.launch {
            walletUseCases.getAllWallets()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "خطا در دریافت حساب‌ها"
                        )
                    }
                }
                .collect { walletList ->
                    _uiState.update {
                        it.copy(
                            wallets = walletList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * حساب انتخاب‌شده را حذف می‌کند.
     *
     * @param wallet حسابی که باید حذف شود
     */
    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletUseCases.deleteWallet(wallet)
        }
    }
}
