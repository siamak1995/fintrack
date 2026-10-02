package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.member.list

import ir.siamak.fintrack.personalaccountant.data.model.Member

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

