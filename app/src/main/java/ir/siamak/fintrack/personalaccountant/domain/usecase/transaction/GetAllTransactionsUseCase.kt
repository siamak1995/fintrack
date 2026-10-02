package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

import ir.siamak.fintrack.personalaccountant.domain.repository.TransactionRepository
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke() = repository.getAllTransactions()
}

