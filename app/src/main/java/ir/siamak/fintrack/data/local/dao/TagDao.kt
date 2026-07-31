package ir.siamak.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id LIMIT 1")
    suspend fun getTagById(id: Long): TagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteTagById(id: Long)

    @Query("SELECT * FROM tags WHERE allowedType = :type AND isDeleted = 0")
    fun getTagsByType(type: TransactionType): Flow<List<TagEntity>>
}
