package ir.siamak.fintrack.storeaccountant.data.mapper

import ir.siamak.fintrack.storeaccountant.data.entity.ProductEntity
import ir.siamak.fintrack.storeaccountant.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        material = material,
        size = size,
        weight = weight,
        stock = stock,
        price = price,
        image = image
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        material = material,
        size = size,
        weight = weight,
        stock = stock,
        price = price,
        image = image
    )
}
