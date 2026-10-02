package ir.siamak.fintrack.personalaccountant.domain.usecase.member

import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.domain.repository.MemberRepository
import javax.inject.Inject

class InsertMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        repository.insertMember(member)
    }
}

