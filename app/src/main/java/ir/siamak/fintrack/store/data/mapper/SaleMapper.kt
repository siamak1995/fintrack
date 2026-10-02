package ir.siamak.fintrack.store.data.mapper

import ir.siamak.fintrack.store.data.entity.SaleEntity
import ir.siamak.fintrack.store.domain.model.Payment
import ir.siamak.fintrack.store.domain.model.Sale
import ir.siamak.fintrack.store.domain.model.Tax

/** Maps sale summaries; line items are stored separately and loaded by a detailed source. */
fun SaleEntity.toDomain() = Sale(id, customerId, sellerId, emptyList(), null, Tax(), Payment(Payment.PaymentType.valueOf(paymentType), finalAmount), subtotal, discountAmount, taxAmount, finalAmount, createdAt)
fun Sale.toEntity() = SaleEntity(id, customerId, sellerId, subtotal, discountAmount, taxAmount, finalAmount, payment.type.name, createdAt)
