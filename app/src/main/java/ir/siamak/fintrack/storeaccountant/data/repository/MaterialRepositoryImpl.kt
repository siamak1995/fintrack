package ir.siamak.fintrack.storeaccountant.data.repository

import ir.siamak.fintrack.storeaccountant.data.dao.RawMaterialDao
import ir.siamak.fintrack.storeaccountant.data.mapper.toDomain
import ir.siamak.fintrack.storeaccountant.data.mapper.toEntity
import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial
import ir.siamak.fintrack.storeaccountant.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaterialRepositoryImpl @Inject constructor(
    private val dao: RawMaterialDao
) : MaterialRepository {
    override fun getAllMaterials(): Flow<List<RawMaterial>> {
        return dao.getAllMaterials().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMaterialById(id: Long): RawMaterial? {
        return dao.getMaterialById(id)?.toDomain()
    }

    override suspend fun insertMaterial(material: RawMaterial) {
        dao.insertMaterial(material.toEntity())
    }

    override suspend fun updateMaterial(material: RawMaterial) {
        dao.updateMaterial(material.toEntity())
    }

    override suspend fun deleteMaterial(material: RawMaterial) {
        dao.deleteMaterial(material.toEntity())
    }
}
