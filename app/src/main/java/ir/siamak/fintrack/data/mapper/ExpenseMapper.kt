package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.ExpenseEntity
import ir.siamak.fintrack.data.model.Expense

fun ExpenseEntity.toModel(): Expense {
    return Expense(
        id = id,
        walletId = walletId,
        title = title,
        amount = amount,
        category = category,
        date = date
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        walletId = walletId,
        title = title,
        amount = amount,
        category = category,
        date = date
    )
}
