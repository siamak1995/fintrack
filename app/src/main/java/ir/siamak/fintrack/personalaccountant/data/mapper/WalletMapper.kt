package ir.siamak.fintrack.personalaccountant.data.mapper

import ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity
import ir.siamak.fintrack.personalaccountant.data.model.Wallet

fun WalletEntity.toModel(): Wallet {
    return Wallet(
        id = id,
        contextId = contextId,
        name = name,
        balance = balance,
        color = color
    )
}

fun Wallet.toEntity(): WalletEntity {
    return WalletEntity(
        id = id,
        contextId = contextId,
        name = name,
        balance = balance,
        color = color
    )
}

