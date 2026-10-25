package ir.siamak.fintrack.store.data.local
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ir.siamak.fintrack.store.data.repository.*
import ir.siamak.fintrack.store.domain.repository.*

@Module @InstallIn(SingletonComponent::class)
object StoreModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context): StoreDatabase =
        Room.databaseBuilder(context, StoreDatabase::class.java, "store_fintrack_db").addMigrations(StoreMigrations.FROM_1_TO_2).build()
    @Provides @Singleton fun store(db: StoreDatabase, profiles: StoreProfilesDataSource): StoreRepository = StoreRepositoryImpl(db, profiles)
    @Provides @Singleton fun seller(db: StoreDatabase, profiles: StoreProfilesDataSource): SellerRepository = SellerRepositoryImpl(db.sellerDao(), profiles)
    @Provides @Singleton fun product(db: StoreDatabase): ProductRepository = ProductRepositoryImpl(db.productDao())
    @Provides @Singleton fun material(db: StoreDatabase): MaterialRepository = MaterialRepositoryImpl(db.rawMaterialDao())
    @Provides @Singleton fun sale(db: StoreDatabase, store: StoreRepository): SaleRepository = SaleRepositoryImpl(db.saleDao(), store)
    @Provides @Singleton fun order(db: StoreDatabase, store: StoreRepository): PreOrderRepository = PreOrderRepositoryImpl(db.preOrderDao(), store)
    @Provides @Singleton fun invoice(db: StoreDatabase): InvoiceRepository = InvoiceRepositoryImpl(db.invoiceDao())
}
