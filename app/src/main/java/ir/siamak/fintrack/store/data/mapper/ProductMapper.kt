package ir.siamak.fintrack.store.data.mapper

import ir.siamak.fintrack.store.data.entity.ProductEntity
import ir.siamak.fintrack.store.domain.model.Product

/** Maps product records. */
fun ProductEntity.toDomain() = Product(id, name, material, size, weight, stock, price, image)
fun Product.toEntity() = ProductEntity(id, name, material, size, weight, stock, price, image)
