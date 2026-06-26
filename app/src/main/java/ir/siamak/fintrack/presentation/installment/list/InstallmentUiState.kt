package ir.siamak.fintrack.presentation.installment.list

import ir.siamak.fintrack.data.model.Installment

/**
 * وضعیت UI صفحه لیست اقساط.
 *
 * این کلاس تمام داده‌هایی که UI نیاز دارد را نگهداری می‌کند.
 * ViewModel این state را مدیریت می‌کند و UI فقط آن را نمایش می‌دهد.
 */
data class InstallmentUiState(

    /**
     * لیست تمام اقساط ثبت شده.
     */
    val installments: List<Installment> = emptyList(),

    /**
     * نشان می‌دهد آیا داده‌ها در حال بارگذاری هستند یا نه.
     */
    val isLoading: Boolean = false,

    /**
     * پیام خطا در صورت وجود مشکل.
     */
    val error: String? = null
)
