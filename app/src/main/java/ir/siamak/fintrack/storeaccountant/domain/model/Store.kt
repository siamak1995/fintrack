package ir.siamak.fintrack.storeaccountant.domain.model

data class Store(
    val id: Long,
    val name: String,
    val brand: String,
    val logoPath: String?,
    val phone: String?,
    val address: String?,
    val description: String?
)
