package ir.siamak.fintrack.domain.usecase.member

import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.domain.repository.MemberRepository
import javax.inject.Inject

class DeleteMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        repository.deleteMember(member)
    }
}
