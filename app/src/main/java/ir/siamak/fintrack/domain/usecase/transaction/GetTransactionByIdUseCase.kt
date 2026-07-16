package ir.siamak.fintrack.domain.usecase.transaction

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.domain.repository.TransactionRepository

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
