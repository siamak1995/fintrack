package ir.siamak.fintrack.storeaccountant.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.siamak.fintrack.storeaccountant.data.entity.RawMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RawMaterialDao {
    @Query("SELECT * FROM raw_materials ORDER BY id DESC")
    fun getAllMaterials(): Flow<List<RawMaterialEntity>>

    @Query("SELECT * FROM raw_materials WHERE id = :id")
    suspend fun getMaterialById(id: Long): RawMaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: RawMaterialEntity)

    @Update
    suspend fun updateMaterial(material: RawMaterialEntity)

    @Delete
    suspend fun deleteMaterial(material: RawMaterialEntity)
}
