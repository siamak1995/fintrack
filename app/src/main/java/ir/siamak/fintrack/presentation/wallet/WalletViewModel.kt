package ir.siamak.fintrack.presentation.wallet

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
 * ویومدل مربوط به مدیریت داده‌ها و منطق صفحه Wallet.
 *
 * این ViewModel وظیفه دارد:
 * - لیست کیف پول‌ها را از UseCase دریافت کند
 * - وضعیت UI را مدیریت کند
 * - عملیات افزودن و حذف کیف پول را انجام دهد
 */
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletUseCases: WalletUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState(isLoading = true))
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWallets()
    }

    /**
     * دریافت لیست کیف پول‌ها از دیتابیس و بروزرسانی State صفحه.
     */
    private fun loadWallets() {
        viewModelScope.launch {
            walletUseCases.getAllWallets()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Unknown error"
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
     * افزودن کیف پول جدید به دیتابیس.
     *
     * @param wallet کیف پولی که باید ذخیره شود
     */
    fun insertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletUseCases.insertWallet(wallet)
        }
    }

    /**
     * حذف کیف پول از دیتابیس.
     *
     * @param wallet کیف پولی که باید حذف شود
     */
    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletUseCases.deleteWallet(wallet)
        }
    }
}
