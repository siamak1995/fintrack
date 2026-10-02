package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.TagDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toTag
import ir.siamak.fintrack.personalaccountant.data.model.Tag
import ir.siamak.fintrack.personalaccountant.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { tags ->
            tags.map { it.toTag() }
        }
    }

    override suspend fun getTagById(id: Long): Tag? {
        return tagDao.getTagById(id)?.toTag()
    }

    override suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag.toEntity())
    }

    override suspend fun deleteTagById(id: Long) {
        tagDao.deleteTagById(id)
    }
}

