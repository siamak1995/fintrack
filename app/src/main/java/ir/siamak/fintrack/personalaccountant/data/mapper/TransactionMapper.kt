package ir.siamak.fintrack.personalaccountant.data.mapper

import ir.siamak.fintrack.personalaccountant.data.local.entity.TagEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionWithTags
import ir.siamak.fintrack.personalaccountant.data.model.Tag
import ir.siamak.fintrack.personalaccountant.data.model.Transaction

/**
 * Maps transaction entity to app transaction model without tags.
 */
fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        contextId = contextId,
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

/**
 * Maps transaction with tags relation to app transaction model.
 */
fun TransactionWithTags.toModel(): Transaction {
    return Transaction(
        id = transaction.id,
        contextId = transaction.contextId,
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

/**
 * Maps tag entity to app tag model.
 */
fun TagEntity.toModel(): Tag {
    return Tag(
        id = id,
        contextId = contextId,
        name = name,
        color = color,
        workspaceId = workspaceId,
        allowedType = allowedType
    )
}

/**
 * Maps transaction model to local transaction entity.
 */
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        contextId = contextId,
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

