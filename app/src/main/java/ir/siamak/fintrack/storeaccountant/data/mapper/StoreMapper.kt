package ir.siamak.fintrack.storeaccountant.data.mapper

import ir.siamak.fintrack.storeaccountant.data.entity.StoreEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Store

fun StoreEntity.toDomain(): Store {
    return Store(
        id = id,
        name = name,
        brand = brand,
        logoPath = logoPath,
        phone = phone,
        address = address,
        description = description
    )
}

fun Store.toEntity(): StoreEntity {
    return StoreEntity(
        id = id,
        name = name,
        brand = brand,
        logoPath = logoPath,
        phone = phone,
        address = address,
        description = description
    )
}
