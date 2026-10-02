package ir.siamak.fintrack.personalaccountant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.siamak.fintrack.personalaccountant.data.local.audit.SyncState
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["toWalletId"],
            onDelete = ForeignKey.SET_NULL // در صورت حذف کیف‌پول مقصد، فیلد null شود
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("walletId"),
        Index("toWalletId"),
        Index("memberId"),
        Index("contextId")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val walletId: Long,
    val toWalletId: Long?, // اضافه شدن فیلد مقصد
    val memberId: Long,
    val date: Long,
    val note: String,

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
    val serverId: Long? = null,
    val contextId: Long = AccountantContextIds.PERSONAL
)

