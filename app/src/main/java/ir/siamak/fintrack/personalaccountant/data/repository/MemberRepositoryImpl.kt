package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.MemberDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val dao: MemberDao
) : MemberRepository {

    override fun getAllMembers(): Flow<List<Member>> {
        return dao.getAllMembers().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getMemberById(id: Long): Member? {
        return dao.getMemberById(id)?.toModel()
    }

    override suspend fun insertMember(member: Member) {
        dao.insertMember(member.toEntity())
    }

    override suspend fun updateMember(member: Member) {
        dao.updateMember(member.toEntity())
    }

    override suspend fun deleteMember(member: Member) {
        dao.deleteMember(member.toEntity())
    }
}

