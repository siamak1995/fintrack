package ir.siamak.fintrack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.local.entity.ExpenseEntity
import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.local.entity.WalletEntity

@Database(
    entities = [
        WalletEntity::class,
        ExpenseEntity::class,
        InstallmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun finTrackDao(): FinTrackDao
}
