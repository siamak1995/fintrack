package ir.siamak.fintrack.presentation.wallet.add_edit_wallet

/**
 * وضعیت نمایشی برای صفحه افزودن یا ویرایش کیف ‌پول.
 *
 * @property name نام وارد شده برای کیف ‌پول.
 * @property balance موجودی اولیه به صورت رشته.
 * @property color رنگ انتخاب شده برای کیف ‌پول (در قالب Hex).
 * @property isLoading وضعیت بارگذاری هنگام ذخیره در دیتابیس.
 * @property isSaved تریگر برای بازگشت به صفحه قبل پس از موفقیت.
 */
data class AddEditWalletState(
    val name: String = "",
    val balance: String = "",
    val color: String = "#4CAF50", // رنگ پیش‌فرض سبز
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
