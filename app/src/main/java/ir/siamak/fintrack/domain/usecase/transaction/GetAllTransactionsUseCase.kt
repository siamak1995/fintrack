package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.domain.repository.TransactionRepository
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke() = repository.getAllTransactions()
}
