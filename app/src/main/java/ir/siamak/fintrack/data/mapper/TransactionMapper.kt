package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.TagEntity
import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.local.entity.TransactionWithTags
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Transaction

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = type,
        categoryName = categoryName,
        walletId = walletId,
        toWalletId = toWalletId,
        memberId = memberId,
        date = date,
        note = note
    )
}

fun TransactionWithTags.toModel(): Transaction {
    return Transaction(
        id = transaction.id,
        amount = transaction.amount,
        type = transaction.type,
        categoryName = transaction.categoryName,
        walletId = transaction.walletId,
        toWalletId = transaction.toWalletId,
        memberId = transaction.memberId,
        date = transaction.date,
        note = transaction.note,
        tags = tags.map { it.toModel() }
    )
}

fun TagEntity.toModel(): Tag {
    return Tag(
        id = id,
        name = name,
        color = color,
        workspaceId = workspaceId,
        allowedType = allowedType
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type,
        categoryName = categoryName,
        walletId = walletId,
        toWalletId = toWalletId,
        memberId = memberId,
        date = date,
        note = note
    )
}
