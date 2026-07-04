package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.model.Tag

fun TagEntity.toTag(): Tag {
    return Tag(
        id = id,
        name = name,
        color = color,
        workspaceId = workspaceId,
        allowedType=allowedType
    )
}

fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = id,
        name = name,
        color = color,
        allowedType = allowedType,

    )
}
