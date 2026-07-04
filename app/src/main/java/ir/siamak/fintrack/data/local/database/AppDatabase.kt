package ir.siamak.fintrack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.siamak.fintrack.data.local.converter.Converters
import ir.siamak.fintrack.data.local.dao.InstallmentDao
import ir.siamak.fintrack.data.local.dao.MemberDao
import ir.siamak.fintrack.data.local.dao.TagDao
import ir.siamak.fintrack.data.local.dao.TransactionDao
import ir.siamak.fintrack.data.local.dao.WalletDao
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.local.entity.MemberEntity
import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.local.entity.TransactionTagCrossRef
import ir.siamak.fintrack.data.local.entity.WalletEntity

@Database(
    entities = [
        WalletEntity::class,
        TransactionEntity::class,
        InstallmentEntity::class,
        MemberEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun memberDao(): MemberDao

    abstract fun installmentDao(): InstallmentDao

    abstract fun tagDao(): TagDao

    abstract fun transactionDao(): TransactionDao
}
