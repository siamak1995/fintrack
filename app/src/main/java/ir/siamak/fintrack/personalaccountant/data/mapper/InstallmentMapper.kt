package ir.siamak.fintrack.personalaccountant.data.mapper

import ir.siamak.fintrack.personalaccountant.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.personalaccountant.data.model.Installment

fun InstallmentEntity.toModel(): Installment {
    return Installment(
        id = id,
        contextId = contextId,
        title = title,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        dueDate = dueDate,
        createdAt = createdAt,
        note = note,
        walletId = walletId,
        isPaid = isPaid

    )
}

fun Installment.toEntity(): InstallmentEntity {
    return InstallmentEntity(
        id = id,
        contextId = contextId,
        title = title,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        dueDate = dueDate,
        createdAt = createdAt,
        note = note,
        walletId = walletId,
        isPaid = isPaid
    )
}

