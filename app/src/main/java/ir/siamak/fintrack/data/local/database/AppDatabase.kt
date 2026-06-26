package ir.siamak.fintrack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.siamak.fintrack.data.local.dao.FinTrackDao
import ir.siamak.fintrack.data.model.Expense
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.data.model.Wallet

@Database(
    entities = [Wallet::class, Expense::class, Installment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun finTrackDao(): FinTrackDao
}
