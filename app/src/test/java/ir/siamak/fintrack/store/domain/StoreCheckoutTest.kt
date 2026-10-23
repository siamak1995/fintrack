package ir.siamak.fintrack.store.domain
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import ir.siamak.fintrack.store.domain.usecase.sale.*
import ir.siamak.fintrack.store.domain.usecase.preorder.PlacePreOrderUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class StoreCheckoutTest {
    private val repository = object : StoreRepository {
        var captured: Invoice? = null
        override fun observeStore() = flowOf<Store?>(null)
        override suspend fun save(store: Store) {}
        override suspend fun checkout(sale: Sale, invoice: Invoice, requestId: String): Long { captured = invoice; return 4 }
        override suspend fun placeOrder(order: PreOrder, invoice: Invoice, requestId: String): Long { captured = invoice; return 5 }
        override suspend fun changeOrderStatus(orderId: Long, status: PreOrderStatus) {}
        override suspend fun receivePayment(invoiceId: Long, amount: Long) {}
    }
    private val workspace = StoreWorkspace(store = Store(1,"فروشگاه","برند"),
        products = listOf(Product(id = 1,name = "ماگ",stock = 5,price = 10_000_000)),
        materials = listOf(RawMaterial(1,"خاک","کیلو",100_000,5.0)))
    private fun sale() = SaleRequest(Customer(name = "مشتری",phone = "0912"),null,mapOf(1L to 1),requestId = "test")
    private fun order() = PreOrderRequest(Customer(name = "مشتری",phone = "0912"),"سفارش اختصاصی",LocalDate.now().plusDays(1).toString(),
        mapOf(1L to 2.0),1_000_000,200_000,100_000,3_000_000,30,true,"test")
    private fun rejects(block: () -> Unit) { assertTrue(runCatching(block).isFailure) }
    @Test fun installmentExampleAndRemainderAreExact() {
        val sale = CheckoutSaleUseCase(repository).preview(sale().copy(installmentMonths = 3, monthlyPercent = 5,firstDueDate = LocalDate.now().plusMonths(1).toString()),workspace)
        assertEquals(11_500_000L,sale.finalAmount)
        assertEquals(sale.finalAmount,sale.payment.installments.sumOf { it.amount })
        assertEquals(0L,sale.payment.paidAmount)
        assertEquals(1L,CalculateInstallmentUseCase()(1,3,0) { it.toString() }.sumOf { it.amount })
    }
    @Test fun cashDiscountTaxAndPaidAmountAreCalculated() {
        val sale = CheckoutSaleUseCase(repository).preview(sale().copy(discount = Discount(Discount.DiscountType.PERCENT,10),taxPercent = 10),workspace)
        assertEquals(9_900_000L,sale.finalAmount)
        assertEquals(sale.finalAmount,sale.payment.paidAmount)
    }
    @Test fun validatesCustomerStockSellerAndTax() {
        val usecase = CheckoutSaleUseCase(repository)
        rejects { usecase.preview(sale().copy(customer = Customer(name = "",phone = "")),workspace) }
        rejects { usecase.preview(sale().copy(quantities = mapOf(1L to 6)),workspace) }
        rejects { usecase.preview(sale().copy(sellerId = 99),workspace) }
        rejects { usecase.preview(sale().copy(taxPercent = 101),workspace) }
        rejects { usecase.preview(sale().copy(quantities = emptyMap()),workspace) }
        rejects { usecase.preview(sale(),workspace.copy(store = null)) }
    }
    @Test fun preventsOverflowAndInvalidSchedule() {
        rejects { CalculateInstallmentUseCase()(Long.MAX_VALUE,60,100) { it.toString() } }
        rejects { CalculateInstallmentUseCase()(1,0,5) { it.toString() } }
        rejects { CheckoutSaleUseCase(repository).preview(sale().copy(quantities = mapOf(1L to 2)),workspace.copy(products = listOf(Product(1,"کالا",stock = 3,price = Long.MAX_VALUE)))) }
        assertEquals(Long.MAX_VALUE,ApplyDiscountUseCase()(Long.MAX_VALUE,Discount(Discount.DiscountType.PERCENT,100)))
    }
    @Test fun preorderPriceAdvanceAndRemaining() {
        val usecase = PlacePreOrderUseCase(repository)
        val order = usecase.preview(order(),workspace)
        assertEquals(1_500_000L,usecase.suggested(order(),workspace))
        assertEquals(900_000L,order.advancePayment)
        assertEquals(2_100_000L,order.agreedPrice-order.advancePayment)
    }
    @Test fun preorderRejectsInvalidMaterialAmountAndAdvance() {
        val usecase = PlacePreOrderUseCase(repository)
        rejects { usecase.preview(order().copy(quantities = mapOf(1L to Double.NaN)),workspace) }
        rejects { usecase.preview(order().copy(quantities = mapOf(1L to 6.0)),workspace) }
        rejects { usecase.preview(order().copy(advance = 101),workspace) }
        rejects { usecase.preview(order().copy(advance = 3_000_001,advanceIsPercent = false),workspace) }
        rejects { usecase.preview(order().copy(deliveryDate = LocalDate.now().minusDays(1).toString()),workspace) }
    }
    @Test fun immutableInvoiceSnapshotRoundTripsAndRetainsCustomer() = runBlocking {
        assertEquals(4L,CheckoutSaleUseCase(repository)(sale(),workspace))
        val invoice = requireNotNull(repository.captured)
        assertEquals(invoice,Json.decodeFromString<Invoice>(Json.encodeToString(invoice)))
        assertEquals("مشتری",invoice.customer.name)
        assertEquals("ماگ",invoice.items.single().productName)
        assertEquals("فروشگاه",invoice.store.name)
    }

    @Test fun rejectsNonFiniteMaterialsAndNegativeShipping() {
        val material = workspace.materials.single()
        assertNotNull(ir.siamak.fintrack.store.core.validation.StoreValidators.material(material.copy(quantity = Double.NaN)))
        assertNotNull(ir.siamak.fintrack.store.core.validation.StoreValidators.material(material.copy(quantity = Double.POSITIVE_INFINITY)))
        assertNotNull(ir.siamak.fintrack.store.core.validation.StoreValidators.material(material.copy(shippingCost = -1)))
        assertNull(ir.siamak.fintrack.store.core.validation.StoreValidators.material(material))
    }
    @Test fun receiptCalculationRejectsOverpaymentAndCancellation() {
        val invoice = Invoice(invoiceNumber = "1", date = LocalDate.now().toString(),store = requireNotNull(workspace.store),
            customer = sale().customer,finalAmount = 100,payment = Payment(Payment.PaymentType.CASH,30),paymentStatus = Invoice.PaymentStatus.UNSETTLED)
        val calculate = ir.siamak.fintrack.store.domain.usecase.invoice.CalculateInvoicePaymentUseCase()
        assertEquals(Invoice.PaymentStatus.SETTLED,calculate(invoice,70).paymentStatus)
        rejects { calculate(invoice,71) }
        rejects { calculate(invoice,-1) }
        rejects { calculate(invoice.copy(cancelled = true),1) }
    }
}
