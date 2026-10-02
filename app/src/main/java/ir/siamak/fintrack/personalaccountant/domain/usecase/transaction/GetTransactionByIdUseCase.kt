package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.domain.repository.TransactionRepository

/**
 * دریافت یک تراکنش با شناسه.
 */
class GetTransactionByIdUseCase(
    private val repository: TransactionRepository
) {

    /**
     * تراکنش مورد نظر را از منبع داده واکشی می‌کند.
     */
    suspend operator fun invoke(id: Long): Transaction? {
        return repository.getTransactionById(id)
    }
}

