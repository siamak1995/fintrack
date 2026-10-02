package ir.siamak.fintrack.storeaccountant.domain.model

data class Seller(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String?,
    val description: String?
)
