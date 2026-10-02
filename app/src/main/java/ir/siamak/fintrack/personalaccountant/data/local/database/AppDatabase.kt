package ir.siamak.fintrack.personalaccountant.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.siamak.fintrack.personalaccountant.data.local.converter.Converters
import ir.siamak.fintrack.personalaccountant.data.local.dao.InstallmentDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.MemberDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.TagDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.TransactionDao
import ir.siamak.fintrack.personalaccountant.data.local.dao.WalletDao
import ir.siamak.fintrack.personalaccountant.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.AccountantContextEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.MemberEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.TagEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionTagCrossRef
import ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity

@Database(
    entities = [
        AccountantContextEntity::class,
        WalletEntity::class,
        TransactionEntity::class,
        InstallmentEntity::class,
        MemberEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun memberDao(): MemberDao

    abstract fun installmentDao(): InstallmentDao

    abstract fun tagDao(): TagDao

    abstract fun transactionDao(): TransactionDao
}

