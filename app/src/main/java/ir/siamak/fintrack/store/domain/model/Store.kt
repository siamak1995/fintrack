package ir.siamak.fintrack.store.domain.model

/** Identifies the shop whose accounting data is being managed. */
data class Store(
    val id: Long = 0,
    val name: String,
    val brand: String = "",
    val logoPath: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val description: String? = null
)
