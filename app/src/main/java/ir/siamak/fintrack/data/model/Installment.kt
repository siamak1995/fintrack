package ir.siamak.fintrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک قسط یا تعهد مالی
 */
data class Installment(
    val id: Long = 0,
    val title: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val dueDate: Long,
    val createdAt: Long,
    val note: String?,
    val walletId: Long,
    val isPaid: Boolean = false
)