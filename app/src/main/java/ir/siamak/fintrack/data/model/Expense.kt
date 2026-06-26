package ir.siamak.fintrack.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک تراکنش مالی
 * @param walletId آیدی کیف پولی که این تراکنش از آن انجام شده
 */
data class Expense(
    val id: Long,
    val walletId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long
)