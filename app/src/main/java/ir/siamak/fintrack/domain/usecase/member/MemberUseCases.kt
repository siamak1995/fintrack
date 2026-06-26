package ir.siamak.fintrack.domain.usecase.member

data class MemberUseCases(
    val getAllMembers: GetAllMembersUseCase,
    val getMemberById: GetMemberByIdUseCase,
    val insertMember: InsertMemberUseCase,
    val updateMember: UpdateMemberUseCase,
    val deleteMember: DeleteMemberUseCase
)
