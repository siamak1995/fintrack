package ir.siamak.fintrack.domain.usecase.installment

import ir.siamak.fintrack.domain.usecase.installments.GetAllInstallmentsUseCase

data class InstallmentUseCases(
    val getAllInstallments: GetAllInstallmentsUseCase
    // بعدا اگر Delete یا Insert خواستی اینجا اضافه کن
)
