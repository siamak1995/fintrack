package ir.siamak.fintrack.storeaccountant.domain.repository

import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    fun getAllMaterials(): Flow<List<RawMaterial>>
    suspend fun getMaterialById(id: Long): RawMaterial?
    suspend fun insertMaterial(material: RawMaterial)
    suspend fun updateMaterial(material: RawMaterial)
    suspend fun deleteMaterial(material: RawMaterial)
}
