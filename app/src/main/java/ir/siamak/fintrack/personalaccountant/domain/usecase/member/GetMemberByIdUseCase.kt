package ir.siamak.fintrack.personalaccountant.domain.usecase.member

import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.domain.repository.MemberRepository
import javax.inject.Inject

class GetMemberByIdUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(id: Long): Member? {
        return repository.getMemberById(id)
    }
}

