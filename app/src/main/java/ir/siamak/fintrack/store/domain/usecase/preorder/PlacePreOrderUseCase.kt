package ir.siamak.fintrack.store.domain.usecase.preorder

import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.model.Invoice.PaymentStatus
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import javax.inject.Inject
import java.time.LocalDate
import java.math.BigDecimal
import java.math.RoundingMode

class PlacePreOrderUseCase @Inject constructor(private val repository: StoreRepository) {
    fun preview(request: PreOrderRequest, workspace: StoreWorkspace): PreOrder {
        require(workspace.store != null) { "ابتدا اطلاعات فروشگاه را ثبت کنید" }
        require(request.customer.name.isNotBlank() && request.customer.phone.isNotBlank()) { "نام و تلفن مشتری الزامی است" }
        require(request.description.isNotBlank()) { "شرح سفارش الزامی است" }
        require(!LocalDate.parse(request.deliveryDate).isBefore(LocalDate.now())) { "تاریخ تحویل نامعتبر است" }
        require(request.requestId.isNotBlank() && request.agreedPrice > 0)
        require(request.productionCost >= 0 && request.profit >= 0 && request.shippingCost >= 0)
        val items = request.quantities.map { (id, count) ->
            val material = workspace.materials.firstOrNull { it.id == id } ?: error("ماده اولیه یافت نشد")
            require(count.isFinite() && count > 0 && count <= material.quantity + 0.000000001) { "موجودی ماده اولیه کافی نیست: " + material.name }
            PreOrderItem(id, material.name, count, material.pricePerUnit)
        }
        val advance = if (request.advanceIsPercent) {
            require(request.advance in 0..100)
            BigDecimal.valueOf(request.agreedPrice).multiply(BigDecimal.valueOf(request.advance)).divide(BigDecimal(100), 0, RoundingMode.HALF_UP).longValueExact()
        } else request.advance
        require(advance in 0..request.agreedPrice) { "پیش‌پرداخت بیش از مبلغ سفارش است" }
        return PreOrder(customerId = request.customer.id, description = request.description, deliveryDate = request.deliveryDate,
            agreedPrice = request.agreedPrice, shippingCost = request.shippingCost, items = items, advancePayment = advance)
    }

    fun materialCost(request: PreOrderRequest, workspace: StoreWorkspace): Long {
        val items = request.quantities.map { (id,quantity) ->
            val material = workspace.materials.firstOrNull { it.id == id } ?: error("ماده اولیه یافت نشد")
            PreOrderItem(id,material.name,quantity,material.pricePerUnit)
        }
        return ir.siamak.fintrack.store.domain.usecase.material.CalculateMaterialCostUseCase()(items)
    }
    fun suggested(request: PreOrderRequest, workspace: StoreWorkspace): Long =
        CalculatePreOrderPriceUseCase()(materialCost(request,workspace),request.productionCost,request.profit,request.shippingCost)
    suspend operator fun invoke(request: PreOrderRequest, workspace: StoreWorkspace): Long {
        val order = preview(request, workspace)
        val invoice = Invoice(invoiceNumber = "P-" + request.requestId, date = LocalDate.now().toString(), store = requireNotNull(workspace.store),
            customer = request.customer, finalAmount = order.agreedPrice, description = order.description, deliveryDate = order.deliveryDate,
            materialItems = order.items, pricing = PreOrderPricing(materialCost(request,workspace),request.productionCost,request.profit,request.shippingCost,suggested(request,workspace)), payment = Payment(Payment.PaymentType.CASH, order.advancePayment), requestId = request.requestId,
            paymentStatus = if (order.advancePayment == order.agreedPrice) PaymentStatus.SETTLED else PaymentStatus.UNSETTLED)
        return repository.placeOrder(order, invoice, request.requestId)
    }
}
