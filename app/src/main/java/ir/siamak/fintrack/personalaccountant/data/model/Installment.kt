package ir.siamak.fintrack.personalaccountant.data.model

import ir.siamak.fintrack.personalaccountant.domain.context.LEGACY_PERSONAL_CONTEXT_ID

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک قسط یا تعهد مالی
 */
data class Installment(
    val id: Long = 0,
    val contextId: Long = LEGACY_PERSONAL_CONTEXT_ID,
    val title: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val dueDate: Long,
    val createdAt: Long,
    val note: String?,
    val walletId: Long,
    val isPaid: Boolean = false,
    val installmentCount: Int = 1,
    val startDate: Long = createdAt,
    val automaticPayment: Boolean = false,
    val principalAmount: Double? = null,
    val annualInterestRate: Double? = null,
    val installmentAmount: Double? = null,
    val openingPaidAmount: Double = paidAmount,
    val endDate: Long? = null,
    val interestMethod: String? = null
)
