package ir.siamak.fintrack.presentation.member.list

import ir.siamak.fintrack.data.model.Member

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
