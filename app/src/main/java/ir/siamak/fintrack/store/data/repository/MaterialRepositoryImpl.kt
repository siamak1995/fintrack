package ir.siamak.fintrack.store.data.repository

import ir.siamak.fintrack.store.data.dao.RawMaterialDao
import ir.siamak.fintrack.store.data.entity.RawMaterialEntity
import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation of [MaterialRepository]. */
class MaterialRepositoryImpl(private val dao: RawMaterialDao) : MaterialRepository {
    override fun observeAll(): Flow<List<RawMaterial>> = dao.observeAll().map { list -> list.map { RawMaterial(it.id, it.name, it.unit, it.pricePerUnit, it.quantity, it.purchaseDate, it.shippingCost) } }
    override suspend fun save(material: RawMaterial) = dao.upsert(RawMaterialEntity(material.id, material.name, material.unit, material.pricePerUnit, material.quantity, material.purchaseDate, material.shippingCost))
    override suspend fun delete(id: Long) = dao.delete(id)
}
