package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت فیلدهای صفحه افزودن تراکنش.
 */
data class AddEditTransactionState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE, // پیش‌فرض روی هزینه
    val selectedCategoryName: String = "سایر",
    val selectedWalletId: Long? = null,
    val note: String = "",
    val wallets: List<Wallet> = emptyList(),// برای نمایش لیست حساب‌ها
    val isLoading: Boolean = false
)
