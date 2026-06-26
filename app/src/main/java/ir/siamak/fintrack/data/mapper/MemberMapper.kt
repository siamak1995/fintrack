package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.MemberEntity
import ir.siamak.fintrack.data.model.Member

fun MemberEntity.toModel(): Member {
    return Member(
        id = id,
        name = name,
        relation = relation,
        color = color,
        icon = icon
    )
}

fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = id,
        name = name,
        relation = relation,
        color = color,
        icon = icon
    )
}
