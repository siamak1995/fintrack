package ir.siamak.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.siamak.fintrack.data.model.TransactionType

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
        Index("memberId")
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
    val note: String
)
