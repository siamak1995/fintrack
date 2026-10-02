package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.list

import ir.siamak.fintrack.personalaccountant.data.model.Tag

data class TagListState(
    val tags: List<Tag> = emptyList()
)

