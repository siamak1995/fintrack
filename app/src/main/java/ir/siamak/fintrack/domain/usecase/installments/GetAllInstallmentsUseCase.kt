package ir.siamak.fintrack.domain.usecase.installments

import ir.siamak.fintrack.domain.repository.InstallmentRepository
import javax.inject.Inject

class GetAllInstallmentsUseCase @Inject constructor(
    private val repository: InstallmentRepository
) {
    operator fun invoke() = repository.getAllInstallments()
}