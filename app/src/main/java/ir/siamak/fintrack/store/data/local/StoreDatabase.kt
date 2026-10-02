package ir.siamak.fintrack.store.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.siamak.fintrack.store.data.dao.*
import ir.siamak.fintrack.store.data.entity.*

/** Isolated Room database for the store-accounting module. */
@Database(entities = [StoreEntity::class, SellerEntity::class, CustomerEntity::class, ProductEntity::class, RawMaterialEntity::class, SaleEntity::class, SaleItemEntity::class, PaymentEntity::class, InstallmentEntity::class, InvoiceEntity::class, PreOrderEntity::class, PreOrderItemEntity::class], version = 1, exportSchema = true)
abstract class StoreDatabase : RoomDatabase() { abstract fun storeDao(): StoreDao; abstract fun sellerDao(): SellerDao; abstract fun customerDao(): CustomerDao; abstract fun productDao(): ProductDao; abstract fun rawMaterialDao(): RawMaterialDao; abstract fun saleDao(): SaleDao; abstract fun invoiceDao(): InvoiceDao; abstract fun preOrderDao(): PreOrderDao }
