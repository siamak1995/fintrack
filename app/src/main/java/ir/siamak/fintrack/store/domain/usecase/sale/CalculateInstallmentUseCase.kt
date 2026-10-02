package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.Installment

/** Builds equal monthly installment amounts plus a monthly percentage charge. */
class CalculateInstallmentUseCase {
    operator fun invoke(baseAmount: Long, months: Int, monthlyPercent: Int, dueDateForMonth: (Int) -> String): List<Installment> {
        require(baseAmount >= 0) { "Base amount cannot be negative" }
        require(months > 0) { "Months must be positive" }
        require(monthlyPercent >= 0) { "Percentage cannot be negative" }
        val total = baseAmount + (baseAmount * monthlyPercent * months / PERCENT_BASE)
        val amountPerMonth = total / months
        val remainder = total % months
        return (1..months).map { month ->
            Installment(month, monthlyPercent, amountPerMonth + if (month == months) remainder else 0, dueDateForMonth(month))
        }
    }

    private companion object { const val PERCENT_BASE = 100L }
}
