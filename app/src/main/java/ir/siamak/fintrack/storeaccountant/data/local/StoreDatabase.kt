package ir.siamak.fintrack.storeaccountant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.siamak.fintrack.storeaccountant.data.dao.CustomerDao
import ir.siamak.fintrack.storeaccountant.data.dao.ProductDao
import ir.siamak.fintrack.storeaccountant.data.dao.RawMaterialDao
import ir.siamak.fintrack.storeaccountant.data.dao.SaleDao
import ir.siamak.fintrack.storeaccountant.data.dao.SellerDao
import ir.siamak.fintrack.storeaccountant.data.dao.StoreDao
import ir.siamak.fintrack.storeaccountant.data.entity.CustomerEntity
import ir.siamak.fintrack.storeaccountant.data.entity.ProductEntity
import ir.siamak.fintrack.storeaccountant.data.entity.RawMaterialEntity
import ir.siamak.fintrack.storeaccountant.data.entity.SaleEntity
import ir.siamak.fintrack.storeaccountant.data.entity.SaleItemEntity
import ir.siamak.fintrack.storeaccountant.data.entity.SellerEntity
import ir.siamak.fintrack.storeaccountant.data.entity.StoreEntity

@Database(
    entities = [
        StoreEntity::class,
        RawMaterialEntity::class,
        SellerEntity::class,
        ProductEntity::class,
        CustomerEntity::class,
        SaleEntity::class,
        SaleItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun rawMaterialDao(): RawMaterialDao
    abstract fun sellerDao(): SellerDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
}

