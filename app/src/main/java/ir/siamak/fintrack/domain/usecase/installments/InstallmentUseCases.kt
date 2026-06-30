package ir.siamak.fintrack.domain.usecase.installments

data class InstallmentUseCases(

    val getAllInstallments:GetAllInstallmentsUseCase,

    val getInstallmentById: GetInstallmentByIdUseCase,

    val insertInstallment:InsertInstallmentUseCase,

    val updateInstallment: UpdateInstallmentUseCase,

    val deleteInstallment:DeleteInstallmentUseCase

)
