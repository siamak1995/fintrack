package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایش‌دهنده یک حساب یا کیف پول
 * @param id شناسه منحصر به فرد
 * @param name نام حساب (مثلاً: بانک ملی، جیب شخصی)
 * @param balance موجودی فعلی
 * @param color کد رنگ برای نمایش گرافیکی در UI
 */
@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val color: String // ذخیره رنگ به صورت Hex (مثل 0xFF2563EB)
)
