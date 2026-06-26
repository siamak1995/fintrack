package ir.siamak.fintrack.presentation.member.add_edit_member

sealed class AddEditMemberEvent {
    data class EnteredName(val value: String) : AddEditMemberEvent()
    data class EnteredRelation(val value: String) : AddEditMemberEvent()
    data class EnteredColor(val value: String) : AddEditMemberEvent()
    object SaveMember : AddEditMemberEvent()
    data class LoadMember(val memberId: Long) : AddEditMemberEvent()
}
