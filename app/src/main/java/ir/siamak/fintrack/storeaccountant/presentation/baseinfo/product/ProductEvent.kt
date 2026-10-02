package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product

import ir.siamak.fintrack.storeaccountant.domain.model.Product

sealed class ProductEvent {
    data class NameChanged(val name: String) : ProductEvent()
    data class MaterialChanged(val material: String) : ProductEvent()
    data class SizeChanged(val size: String) : ProductEvent()
    data class WeightChanged(val weight: String) : ProductEvent()
    data class StockChanged(val stock: String) : ProductEvent()
    data class PriceChanged(val price: String) : ProductEvent()
    object SaveProduct : ProductEvent()
    data class DeleteProduct(val product: Product) : ProductEvent()
}
