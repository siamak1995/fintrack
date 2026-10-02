package ir.siamak.fintrack.store.domain.usecase.preorder

/** Calculates a prepaid amount from either a fixed amount or percentage. */
class CalculateAdvancePaymentUseCase {
    fun fromPercent(total: Long, percent: Int): Long { require(total >= 0 && percent in 0..100); return total * percent / 100 }
    fun fromAmount(total: Long, amount: Long): Long { require(total >= 0 && amount in 0..total); return amount }
    fun remaining(total: Long, advance: Long): Long { require(total >= 0 && advance in 0..total); return total - advance }
}
