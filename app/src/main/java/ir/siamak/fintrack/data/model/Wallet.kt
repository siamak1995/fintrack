package ir.siamak.fintrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک حساب یا کیف پول
 * @param id شناسه منحصر به فرد
 * @param name نام حساب (مثلاً: بانک ملی، جیب شخصی)
 * @param balance موجودی فعلی
 * @param color کد رنگ برای نمایش گرافیکی در UI
 */
data class Wallet(
    val id: Long = 0L,
    val name: String,
    val balance: Double,
    val color: String
)
