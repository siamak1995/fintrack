package ir.siamak.fintrack.storeaccountant.data.mapper

import ir.siamak.fintrack.storeaccountant.data.entity.SellerEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Seller

fun SellerEntity.toDomain(): Seller {
    return Seller(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        address = address,
        description = description
    )
}

fun Seller.toEntity(): SellerEntity {
    return SellerEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        address = address,
        description = description
    )
}
