package ir.siamak.fintrack.store.core.validation

import ir.siamak.fintrack.store.domain.model.Customer
import ir.siamak.fintrack.store.domain.model.RawMaterial

/** Stateless input validation shared by store form use cases. */
object StoreValidators {
    fun customer(customer: Customer): String? = when { customer.name.isBlank() -> "نام مشتری الزامی است"; customer.phone.isBlank() -> "شماره تماس الزامی است"; else -> null }
    fun material(material: RawMaterial): String? = when { material.name.isBlank() -> "نام ماده الزامی است"; material.unit.isBlank() -> "واحد الزامی است"; material.pricePerUnit < 0 -> "قیمت نامعتبر است"; material.quantity < 0 -> "موجودی نامعتبر است"; else -> null }
}
