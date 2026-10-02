package ir.siamak.fintrack.storeaccountant.data.mapper

import ir.siamak.fintrack.storeaccountant.data.entity.RawMaterialEntity
import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial

fun RawMaterialEntity.toDomain(): RawMaterial {
    return RawMaterial(
        id = id,
        name = name,
        unit = unit,
        pricePerUnit = pricePerUnit,
        quantity = quantity,
        purchaseDate = purchaseDate,
        shippingCost = shippingCost
    )
}

fun RawMaterial.toEntity(): RawMaterialEntity {
    return RawMaterialEntity(
        id = id,
        name = name,
        unit = unit,
        pricePerUnit = pricePerUnit,
        quantity = quantity,
        purchaseDate = purchaseDate,
        shippingCost = shippingCost
    )
}
