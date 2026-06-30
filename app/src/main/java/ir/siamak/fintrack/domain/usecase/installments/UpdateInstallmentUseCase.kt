package ir.siamak.fintrack.domain.usecase.installments

import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.domain.repository.InstallmentRepository
import javax.inject.Inject

class UpdateInstallmentUseCase @Inject constructor(
    private val repository: InstallmentRepository
){

    suspend operator fun invoke(
        installment: Installment
    ){

        repository.updateInstallment(
            installment
        )

    }

}