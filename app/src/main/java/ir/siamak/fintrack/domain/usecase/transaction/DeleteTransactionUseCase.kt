package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.deleteTransaction(transaction)
    }
}
