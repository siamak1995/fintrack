package ir.siamak.fintrack.presentation.member.add_edit_member

data class AddEditMemberState(
    val name: String = "",
    val relation: String = "",
    val color: String = "#2196F3",
    val icon: String = "ic_default_user",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
