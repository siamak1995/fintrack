package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsByWalletUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(walletId: Long) = repository.getTransactionsByWallet(walletId)
}
