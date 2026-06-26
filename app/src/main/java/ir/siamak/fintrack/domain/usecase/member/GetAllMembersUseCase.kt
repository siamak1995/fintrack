package ir.siamak.fintrack.domain.usecase.member

import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMembersUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    operator fun invoke(): Flow<List<Member>> {
        return repository.getAllMembers()
    }
}
