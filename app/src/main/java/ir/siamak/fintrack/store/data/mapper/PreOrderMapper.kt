package ir.siamak.fintrack.store.data.mapper

import ir.siamak.fintrack.store.data.entity.PreOrderEntity
import ir.siamak.fintrack.store.domain.model.PreOrder
import ir.siamak.fintrack.store.domain.model.PreOrderStatus

/** Maps custom pre-order records. */
fun PreOrderEntity.toDomain() = PreOrder(id, customerId, description, deliveryDate, agreedPrice, shippingCost, PreOrderStatus.valueOf(status), advancePayment = advancePayment)
fun PreOrder.toEntity() = PreOrderEntity(id, customerId, description, deliveryDate, agreedPrice, shippingCost, status.name, advancePayment)
