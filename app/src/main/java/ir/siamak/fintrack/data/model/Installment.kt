package ir.siamak.fintrack.data.model

/**
 * نمایش‌دهنده یک قسط یا تعهد مالی
 */
data class Installment(
    val id: Long = 0,
    val title: String,
    val totalAmount: Double, // مبلغ کل وام/قسط
    val paidAmount: Double,  // مبلغ پرداخت شده تا کنون
    val dueDate: Long        // تاریخ سررسید بعدی
)
