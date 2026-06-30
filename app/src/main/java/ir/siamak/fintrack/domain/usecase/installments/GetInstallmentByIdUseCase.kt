package ir.siamak.fintrack.domain.usecase.installments

import ir.siamak.fintrack.domain.repository.InstallmentRepository
import javax.inject.Inject

class GetInstallmentByIdUseCase @Inject constructor(
    private val repository: InstallmentRepository
){

    suspend operator fun invoke(
        id:Long
    ) = repository.getInstallmentById(id)

}