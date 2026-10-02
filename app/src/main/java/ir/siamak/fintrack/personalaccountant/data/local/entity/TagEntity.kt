package ir.siamak.fintrack.personalaccountant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.siamak.fintrack.personalaccountant.data.local.audit.SyncState
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val color: Long? = null,
    val workspaceId: Long? = null,
    val allowedType: TransactionType,

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

