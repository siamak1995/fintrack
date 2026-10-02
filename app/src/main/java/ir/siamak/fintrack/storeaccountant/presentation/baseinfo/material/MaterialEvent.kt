package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material

import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial

sealed class MaterialEvent {
    data class NameChanged(val name: String) : MaterialEvent()
    data class UnitChanged(val unit: String) : MaterialEvent()
    data class PriceChanged(val price: String) : MaterialEvent()
    data class QuantityChanged(val quantity: String) : MaterialEvent()
    data class PurchaseDateChanged(val date: String) : MaterialEvent()
    data class ShippingCostChanged(val cost: String) : MaterialEvent()
    object SaveMaterial : MaterialEvent()
    data class DeleteMaterial(val material: RawMaterial) : MaterialEvent()
}
