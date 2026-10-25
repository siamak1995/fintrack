package ir.siamak.fintrack.store.domain.usecase.store
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.*
import ir.siamak.fintrack.store.core.validation.StoreValidators
import javax.inject.Inject

/** Catalog validation and order lifecycle rules used by forms. */
class StoreCommandsUseCase @Inject constructor(
    private val stores: StoreRepository,
    private val sellers: SellerRepository,
    private val products: ProductRepository,
    private val materials: MaterialRepository
) {
    suspend fun saveStore(value: Store) {
        require(value.name.isNotBlank()) { "نام فروشگاه الزامی است" }
        stores.save(value)
    }
    suspend fun selectStore(id: Long) = stores.selectStore(id)
    suspend fun syncInvoice(id: Long) = stores.syncInvoice(id)
    suspend fun deleteStore(id: Long) = stores.deleteStore(id)
    suspend fun saveBrand(value: Brand) = stores.saveBrand(value)
    suspend fun deleteBrand(id: String) = stores.deleteBrand(id)
    suspend fun saveSeller(value: Seller) {
        require(value.firstName.isNotBlank() && value.lastName.isNotBlank() && value.phone.isNotBlank()) { "نام، نام خانوادگی و تلفن فروشنده الزامی است" }
        sellers.save(value)
    }

    suspend fun saveProduct(value: Product) {
        if(value.id > 0) ir.siamak.fintrack.store.domain.usecase.product.UpdateProductUseCase(products)(value)
        else ir.siamak.fintrack.store.domain.usecase.product.CreateProductUseCase(products)(value)
    }
    suspend fun saveMaterial(value: RawMaterial) {
        if(value.id > 0) ir.siamak.fintrack.store.domain.usecase.material.UpdateMaterialUseCase(materials)(value)
        else ir.siamak.fintrack.store.domain.usecase.material.AddMaterialUseCase(materials)(value)
    }
    suspend fun deleteProduct(id: Long) = products.delete(id)
    suspend fun deleteSeller(id: Long) = sellers.delete(id)
    suspend fun deleteMaterial(id: Long) = materials.delete(id)
    suspend fun changeStatus(id: Long, status: PreOrderStatus) {
        stores.changeOrderStatus(id, status)
    }
    suspend fun receivePayment(id: Long, amount: Long) {
        require(amount > 0) { "مبلغ پرداخت باید مثبت باشد" }
        stores.receivePayment(id, amount)
    }
}
