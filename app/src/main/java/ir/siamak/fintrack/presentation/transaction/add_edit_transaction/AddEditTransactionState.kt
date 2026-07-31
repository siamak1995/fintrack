package ir.siamak.fintrack.presentation.transaction.add_edit_transaction

import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.Wallet

/**
 * وضعیت کامل صفحه ثبت/ویرایش تراکنش.
 */
data class AddEditTransactionState(

    val filteredTags: List<Tag> = emptyList(),
    /**
     * اگر null باشد یعنی در حالت ثبت جدید هستیم.
     * اگر مقدار داشته باشد یعنی صفحه در حالت ویرایش است.
     */
    val currentTransactionId: Long? = null,

    /**
     * مقدار خام مبلغ بدون فرمت نمایشی.
     */
    val amountRaw: String = "",

    /**
     * مبلغ فرمت‌شده برای نمایش در UI.
     */
    val amount: String = "",

    /**
     * نوع تراکنش.
     */
    val type: TransactionType = TransactionType.EXPENSE,

    /**
     * نام دسته‌بندی انتخاب‌شده.
     */
    val selectedCategoryName: String = "سایر",

    /**
     * شناسه حساب مبدا.
     */
    val selectedWalletId: Long? = null,

    /**
     * شناسه عضو انتخاب‌شده.
     */
    val selectedMemberId: Long? = null,

    /**
     * شناسه حساب مقصد برای انتقال.
     */
    val selectedToWalletId: Long? = null,

    /**
     * یادداشت تراکنش.
     */
    val note: String = "",

    /**
     * لیست حساب‌ها برای نمایش در UI.
     */
    val wallets: List<Wallet> = emptyList(),

    /**
     * لیست اعضا برای نمایش در UI.
     */
    val members: List<Member> = emptyList(),

    /**
     * لیست تگ‌ها برای نمایش در UI.
     */
    val tags: List<Tag> = emptyList(),

    /**
     * شناسه تگ‌های انتخاب‌شده.
     */
    val selectedTagIds: List<Long> = emptyList(),

    /**
     * وضعیت بارگذاری صفحه.
     */
    val isLoading: Boolean = false
)
