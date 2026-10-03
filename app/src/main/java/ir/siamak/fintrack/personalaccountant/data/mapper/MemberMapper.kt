package ir.siamak.fintrack.personalaccountant.data.mapper

import ir.siamak.fintrack.personalaccountant.data.local.entity.MemberEntity
import ir.siamak.fintrack.personalaccountant.data.model.Member

fun MemberEntity.toModel(): Member {
    return Member(
        id = id,
        contextId = contextId,
        name = name,
        relation = relation,
        color = color,
        icon = icon
    )
}

fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id = id,
        contextId = contextId,
        name = name,
        relation = relation,
        color = color,
        icon = icon
    )
}

