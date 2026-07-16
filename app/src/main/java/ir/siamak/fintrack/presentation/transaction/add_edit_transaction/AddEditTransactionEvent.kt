package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import ir.siamak.fintrack.data.model.TransactionType

/**
 * رویدادهای قابل ارسال از UI به ViewModel در صفحه ثبت/ویرایش تراکنش.
 */
sealed class AddEditTransactionEvent {

    /**
     * تغییر مبلغ واردشده توسط کاربر.
     */
    data class EnteredAmount(val value: String) : AddEditTransactionEvent()

    /**
     * تغییر نوع تراکنش.
     */
    data class TypeChanged(val type: TransactionType) : AddEditTransactionEvent()

    /**
     * انتخاب دسته‌بندی تراکنش.
     */
    data class CategorySelected(val categoryName: String) : AddEditTransactionEvent()

    /**
     * انتخاب حساب مبدا.
     */
    data class WalletSelected(val walletId: Long) : AddEditTransactionEvent()

    /**
     * انتخاب حساب مقصد برای انتقال.
     */
    data class ToWalletSelected(val walletId: Long) : AddEditTransactionEvent()

    /**
     * انتخاب عضو.
     */
    data class MemberSelected(val memberId: Long) : AddEditTransactionEvent()

    /**
     * تغییر یادداشت.
     */
    data class EnteredNote(val value: String) : AddEditTransactionEvent()

    /**
     * انتخاب یا برداشتن یک تگ.
     */
    data class TagToggled(val tagId: Long) : AddEditTransactionEvent()

    /**
     * ذخیره یا ویرایش تراکنش.
     */
    object SaveTransaction : AddEditTransactionEvent()

    /**
     * حذف تراکنش فعلی.
     */
    object DeleteTransaction : AddEditTransactionEvent()
}
