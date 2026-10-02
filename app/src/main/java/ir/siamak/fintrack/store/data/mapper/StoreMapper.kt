package ir.siamak.fintrack.store.data.mapper

import ir.siamak.fintrack.store.data.entity.StoreEntity
import ir.siamak.fintrack.store.domain.model.Store

/** Maps store records without exposing Room types beyond the data layer. */
fun StoreEntity.toDomain() = Store(id, name, brand, logoPath, phone, address, description)
fun Store.toEntity() = StoreEntity(id, name, brand, logoPath, phone, address, description)
