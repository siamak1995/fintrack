package ir.siamak.fintrack.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * قرارداد ناوبری (Navigation Contract) کل اپلیکیشن.
 *
 * این sealed class تمام routeهای اصلی و فرعی پروژه را در یک نقطه نگه می‌دارد
 * تا navigation به صورت type-safe انجام شود و وابستگی به route stringهای پراکنده از بین برود.
 *
 * مسیرها به دو دسته اصلی تقسیم می‌شوند:
 *
 * 1) صفحات اصلی:
 * - این صفحات در bottom bar نمایش داده می‌شوند.
 * - کاربر بین آن‌ها جابه‌جا می‌شود.
 *
 * 2) صفحات فرعی:
 * - از داخل صفحات اصلی باز می‌شوند.
 * - معمولاً bottom bar در آن‌ها نمایش داده نمی‌شود.
 *
 * نکته:
 * در لایه data/domain هنوز نام Wallet استفاده می‌شود،
 * اما در UI می‌توان آن را به صورت «حساب» یا «حساب بانکی» نمایش داد.
 */
sealed class Screen {

    /**
     * صفحه اصلی داشبورد.
     *
     * این صفحه نمای کلی وضعیت مالی کاربر را نمایش می‌دهد؛
     * مانند خلاصه مالی، موجودی کل، میان‌برها و اطلاعات مهم ماه جاری.
     */
    @Serializable
    object Dashboard : Screen()

    /**
     * صفحه لیست حساب‌ها.
     *
     * در ساختار فعلی پروژه این بخش با نام Wallet شناخته می‌شود،
     * اما در UI نقش «حساب‌ها / حساب‌های بانکی» را دارد.
     */
    @Serializable
    object WalletList : Screen()

    /**
     * صفحه افزودن یا ویرایش حساب.
     *
     * اگر [walletId] تهی باشد، صفحه در حالت افزودن حساب جدید است.
     * اگر مقدار داشته باشد، صفحه در حالت ویرایش حساب موجود باز می‌شود.
     *
     * @property walletId شناسه حساب برای ویرایش، یا null برای ایجاد حساب جدید
     */
    @Serializable
    data class AddEditWallet(val walletId: Long? = null) : Screen()

    /**
     * صفحه اعضا.
     *
     * برای مدیریت اعضای خانواده یا افرادی که در گزارش‌ها و تراکنش‌ها
     * می‌توانند نقش داشته باشند.
     */
    @Serializable
    object Members : Screen()

    /**
     * صفحه اقساط و تعهدات مالی.
     *
     * برای ثبت پرداخت‌های دوره‌ای، اقساط، بدهی‌ها و پیگیری مانده تعهدات.
     */
    @Serializable
    object Installments : Screen()

    /**
     * صفحه گزارشات.
     *
     * برای نمایش گزارش‌های مالی بر اساس بازه زمانی، شخص، نوع تراکنش
     * و سایر فیلترهای تحلیلی.
     */
    @Serializable
    object Reports : Screen()

    /**
     * صفحه تنظیمات.
     *
     * برای تنظیمات عمومی اپلیکیشن و ترجیحات کاربر.
     */
    @Serializable
    object Settings : Screen()

    /**
     * صفحه افزودن یا ویرایش تراکنش (درآمد/هزینه).
     *
     * @property transactionId شناسه تراکنش برای ویرایش، یا null برای ثبت جدید
     */
    @Serializable
    data class AddEditTransaction(val transactionId: Long? = null) : Screen()

}
