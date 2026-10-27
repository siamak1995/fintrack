package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** A sellable product. Only [name] is mandatory at entry time. */
@Serializable
data class Product(
    val id: Long = 0,
    val name: String,
    val material: String? = null,
    val size: String? = null,
    val weight: String? = null,
    val stock: Int = 0,
    val price: Long = 0,
    val image: String? = null,
    val color: String? = null
)
