package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(id: Long): Tag?
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTagById(id: Long)
}
