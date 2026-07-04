package ir.siamak.fintrack.presentation.baseinfo.tags.list

sealed class TagListEvent {
    data class Delete(val id: Long) : TagListEvent()
}
