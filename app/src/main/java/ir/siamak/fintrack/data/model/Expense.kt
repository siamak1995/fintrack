package ir.siamak.fintrack.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک تراکنش مالی
 * @param walletId آیدی کیف پولی که این تراکنش از آن انجام شده
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val walletId: Long,
    val title: String,
    val amount: Double,
    val category: String, // دسته بندی (مثلاً: خوراک، حمل و نقل)
    val date: Long // ذخیره زمان به صورت Timestamp (System.currentTimeMillis)
)
