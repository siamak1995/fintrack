package ir.siamak.fintrack.presentation.wallet.add_edit_wallet

/**
 * رویدادهای مربوط به صفحه افزودن یا ویرایش کیف ‌پول.
 *
 * این Sealed Class به ViewModel کمک می‌کند تا تمامی اکشن‌های کاربر را
 * به صورت متمرکز در یک تابع مدیریت کند.
 */
sealed class AddEditWalletEvent {
    /**
     * تغییر در متن نام کیف ‌پول.
     * @param value مقدار جدید وارد شده توسط کاربر.
     */
    data class EnteredName(val value: String): AddEditWalletEvent()

    /**
     * تغییر در مقدار موجودی کیف ‌پول.
     * @param value مبلغ جدید وارد شده (به صورت رشته).
     */
    data class EnteredBalance(val value: String): AddEditWalletEvent()


    /**
     * تغییر در رنگ انتخاب شده برای کیف ‌پول.
     * @param value رنگ جدید در قالب Hex.
     */
    data class EnteredColor(val value: String): AddEditWalletEvent()


    /**
     * درخواست برای ذخیره نهایی اطلاعات در دیتابیس.
     */
    object SaveWallet: AddEditWalletEvent()

    data class LoadWallet(val walletId: Long) : AddEditWalletEvent()
}
