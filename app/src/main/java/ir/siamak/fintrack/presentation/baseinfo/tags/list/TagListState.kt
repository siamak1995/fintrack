package ir.siamak.fintrack.presentation.baseinfo.tags.list

import ir.siamak.fintrack.data.model.Tag

data class TagListState(
    val tags: List<Tag> = emptyList()
)
