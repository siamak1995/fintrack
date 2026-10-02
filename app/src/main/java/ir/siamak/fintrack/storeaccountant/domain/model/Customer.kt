package ir.siamak.fintrack.storeaccountant.domain.model

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String?
)
