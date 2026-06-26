package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.TransactionEntity
import ir.siamak.fintrack.data.model.Transaction

fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = type,
        categoryName = categoryName,
        walletId = walletId,
        memberId = memberId,
        date = date,
        note = note
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type,
        categoryName = categoryName,
        walletId = walletId,
        memberId = memberId,
        date = date,
        note = note
    )
}
