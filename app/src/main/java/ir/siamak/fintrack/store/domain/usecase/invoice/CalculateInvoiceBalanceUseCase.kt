package ir.siamak.fintrack.store.domain.usecase.invoice
import ir.siamak.fintrack.store.domain.model.*
class CalculateInvoiceBalanceUseCase {
    operator fun invoke(invoice: Invoice): Long = invoice.finalAmount - (invoice.payment?.paidAmount ?: 0L)
    operator fun invoke(order: PreOrder): Long = order.agreedPrice - order.advancePayment
}
