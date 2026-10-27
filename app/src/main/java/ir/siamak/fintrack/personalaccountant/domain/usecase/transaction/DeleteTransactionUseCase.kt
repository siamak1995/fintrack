package ir.siamak.fintrack.personalaccountant.domain.usecase.transaction

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.repository.PersonalLedger
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(private val ledger: PersonalLedger) {
    suspend operator fun invoke(transaction: Transaction) { ledger.delete(transaction.id) }
}
