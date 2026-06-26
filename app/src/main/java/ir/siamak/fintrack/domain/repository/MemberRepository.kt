package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    fun getAllMembers(): Flow<List<Member>>

    suspend fun getMemberById(id: Long): Member?

    suspend fun insertMember(member: Member)

    suspend fun updateMember(member: Member)

    suspend fun deleteMember(member: Member)
}
