package ir.siamak.fintrack.store.domain.usecase.invoice
import ir.siamak.fintrack.store.domain.model.*
class CalculateInvoicePaymentUseCase {
    operator fun invoke(invoice: Invoice, amount: Long): Invoice {
        require(!invoice.cancelled && amount > 0) { "مبلغ پرداخت نامعتبر است یا فاکتور لغو شده است" }
        val payment = invoice.payment ?: Payment(Payment.PaymentType.CASH,0)
        val paid = Math.addExact(payment.paidAmount,amount)
        require(paid <= invoice.finalAmount) { "مبلغ بیش از مانده است" }
        return invoice.copy(payment = payment.copy(paidAmount = paid),
            paymentStatus = if(paid == invoice.finalAmount) Invoice.PaymentStatus.SETTLED else invoice.paymentStatus)
    }
}
