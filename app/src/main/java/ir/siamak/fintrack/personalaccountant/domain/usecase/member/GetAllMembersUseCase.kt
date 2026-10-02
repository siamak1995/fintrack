package ir.siamak.fintrack.personalaccountant.domain.usecase.member

import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMembersUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    operator fun invoke(): Flow<List<Member>> {
        return repository.getAllMembers()
    }
}

