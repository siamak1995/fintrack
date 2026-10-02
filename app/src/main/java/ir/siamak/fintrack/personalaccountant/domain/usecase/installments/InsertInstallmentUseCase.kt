package ir.siamak.fintrack.personalaccountant.domain.usecase.installments

import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.repository.InstallmentRepository
import javax.inject.Inject

class InsertInstallmentUseCase @Inject constructor(
    private val repository: InstallmentRepository
) {
    suspend operator fun invoke(installment: Installment) {
        repository.insertInstallment(installment)
    }
}

