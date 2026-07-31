package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.siamak.fintrack.data.local.audit.SyncState

/**
 * نمایش‌دهنده یک حساب یا کیف پول
 * @param id شناسه منحصر به فرد
 * @param name نام حساب (مثلاً: بانک ملی، جیب شخصی)
 * @param balance موجودی فعلی
 * @param color کد رنگ برای نمایش گرافیکی در UI
 */
@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val color: String,


    /**
     * کلاس پایه تمام Entityهای برنامه.
     *
     * تمام مدل‌های قابل ذخیره در دیتابیس باید از این کلاس ارث‌بری کنند.
     *
     * این اطلاعات برای موارد زیر استفاده می‌شوند:
     *
     * - شناسایی رکورد
     * - همگام‌سازی ابری
     * - حذف نرم (Soft Delete)
     * - تشخیص تعارض نسخه‌ها
     * - تاریخچه تغییرات
     */
    open val createdAt: Long = System.currentTimeMillis(),

    /**
     * آخرین زمان ویرایش.
     */
    open val updatedAt: Long = System.currentTimeMillis(),

    /**
     * حذف نرم.
     */
    open val isDeleted: Boolean = false,

    /**
     * نسخه رکورد برای همگام‌سازی.
     */
    open val version: Int = 1,



    /**
     * کلاس پایه برای بک آپ.
     */
    val syncState: SyncState = SyncState.LOCAL_ONLY,
    val serverId: Long? = null
)
