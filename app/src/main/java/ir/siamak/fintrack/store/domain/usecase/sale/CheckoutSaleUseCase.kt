package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import javax.inject.Inject
import java.time.LocalDate
import java.math.BigDecimal
import java.math.RoundingMode

class CheckoutSaleUseCase @Inject constructor(private val repository: StoreRepository) {
    fun preview(request: SaleRequest, workspace: StoreWorkspace): Sale {
        require(request.customer.name.isNotBlank() && request.customer.phone.isNotBlank()) { "نام و تلفن مشتری الزامی است" }
        require(workspace.store != null) { "ابتدا اطلاعات فروشگاه را ثبت کنید" }
        require(request.quantities.isNotEmpty()) { "حداقل یک کالا انتخاب کنید" }
        require(request.requestId.isNotBlank())
        require(request.taxPercent in 0..100 && request.installmentMonths in 0..60 && request.monthlyPercent in 0..100)
        require(request.sellerId == null || workspace.sellers.any { it.id == request.sellerId }) { "فروشنده نامعتبر است" }
        val items = request.quantities.map { (id, count) ->
            val product = workspace.products.firstOrNull { it.id == id } ?: error("کالا یافت نشد")
            require(count > 0 && count <= product.stock) { "موجودی کافی نیست: " + product.name }
            SaleItem(id, product.name, count, product.price)
        }
        val subtotal = items.fold(0L) { total, item -> Math.addExact(total, Math.multiplyExact(item.unitPrice, item.quantity.toLong())) }
        val discount = ApplyDiscountUseCase()(subtotal, request.discount)
        val taxable = subtotal - discount
        val tax = BigDecimal.valueOf(taxable).multiply(BigDecimal.valueOf(request.taxPercent.toLong())).divide(BigDecimal(100), 0, RoundingMode.HALF_UP).longValueExact()
        val baseAmount = Math.addExact(taxable, tax)
        val schedule = if (request.installmentMonths > 0) {
            val first = LocalDate.parse(request.firstDueDate)
            require(!first.isBefore(LocalDate.now())) { "سررسید نباید در گذشته باشد" }
            CalculateInstallmentUseCase()(baseAmount, request.installmentMonths, request.monthlyPercent) { first.plusMonths(it.toLong() - 1).toString() }
        } else emptyList()
        val final = if (schedule.isEmpty()) baseAmount else schedule.fold(0L) { total, item -> Math.addExact(total, item.amount) }
        val payment = Payment(if (schedule.isEmpty()) Payment.PaymentType.CASH else Payment.PaymentType.INSTALLMENT, if (schedule.isEmpty()) final else 0, schedule)
        return Sale(customerId = request.customer.id, sellerId = request.sellerId, items = items, discount = request.discount,
            tax = Tax(request.taxPercent), payment = payment, subtotal = subtotal, discountAmount = discount,
            taxAmount = tax, finalAmount = final, createdAt = LocalDate.now().toString())
    }
    suspend operator fun invoke(request: SaleRequest, workspace: StoreWorkspace): Long {
        val sale = preview(request, workspace)
        val invoice = Invoice(invoiceNumber = "S-" + request.requestId, date = sale.createdAt, store = requireNotNull(workspace.store),
            seller = workspace.sellers.firstOrNull { it.id == request.sellerId }, customer = request.customer, items = sale.items,
            discount = sale.discount, tax = sale.tax, discountAmount = sale.discountAmount, taxAmount = sale.taxAmount, payment = sale.payment, finalAmount = sale.finalAmount,
            requestId = request.requestId, paymentStatus = if (sale.payment.paidAmount == sale.finalAmount) PaymentStatus.SETTLED else PaymentStatus.INSTALLMENT)
        return repository.checkout(sale, invoice, request.requestId)
    }
}
