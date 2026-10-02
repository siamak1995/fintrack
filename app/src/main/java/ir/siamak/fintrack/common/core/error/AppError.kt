package ir.siamak.fintrack.common.core.error

/** Stable, non-sensitive errors that can be converted into a Persian UI message. */
sealed interface AppError {
    data object Database : AppError
    data object Navigation : AppError
    data object InvalidInput : AppError
    data object FileOperation : AppError
    data object Calculation : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}

/** Provides user-safe Persian messages without exposing exception details. */
fun AppError.toPersianMessage(): String = when (this) {
    AppError.Database -> "ذخیره یا بازیابی اطلاعات با خطا مواجه شد."
    AppError.Navigation -> "باز کردن صفحه موردنظر ممکن نشد."
    AppError.InvalidInput -> "اطلاعات واردشده معتبر نیست."
    AppError.FileOperation -> "عملیات فایل با خطا مواجه شد."
    AppError.Calculation -> "محاسبه مالی با خطا مواجه شد."
    is AppError.Unknown -> "خطای غیرمنتظره‌ای رخ داد."
}
