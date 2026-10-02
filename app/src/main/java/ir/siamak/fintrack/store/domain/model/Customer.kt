package ir.siamak.fintrack.store.domain.model

/** Customer information captured for a sale or pre-order. */
data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String? = null
)
