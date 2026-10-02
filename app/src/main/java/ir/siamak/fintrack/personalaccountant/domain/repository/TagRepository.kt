package ir.siamak.fintrack.personalaccountant.domain.repository

import ir.siamak.fintrack.personalaccountant.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun getTagById(id: Long): Tag?
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTagById(id: Long)
}

