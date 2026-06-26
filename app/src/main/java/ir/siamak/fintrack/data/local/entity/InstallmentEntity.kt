package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک قسط یا تعهد مالی
 */
@Entity(tableName = "installment")
data class InstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val totalAmount: Double, // مبلغ کل وام/قسط
    val paidAmount: Double,  // مبلغ پرداخت شده تا کنون
    val dueDate: Long        // تاریخ سررسید بعدی
)
