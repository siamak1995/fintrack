package ir.siamak.fintrack.data.mapper

import ir.siamak.fintrack.data.local.entity.WalletEntity
import ir.siamak.fintrack.data.model.Wallet

fun WalletEntity.toModel(): Wallet {
    return Wallet(
        id = id,
        name = name,
        balance = balance,
        color = color
    )
}

fun Wallet.toEntity(): WalletEntity {
    return WalletEntity(
        id = id,
        name = name,
        balance = balance,
        color = color
    )
}
