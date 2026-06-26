package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.InstallmentEntity
import ir.siamak.fintrack.data.model.Installment

fun InstallmentEntity.toModel(): Installment {
    return Installment(
        id = id,
        title = title,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        dueDate = dueDate
    )
}

fun Installment.toEntity(): InstallmentEntity {
    return InstallmentEntity(
        id = id,
        title = title,
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        dueDate = dueDate
    )
}
