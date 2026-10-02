package ir.siamak.fintrack.storeaccountant.domain.model

data class Product(
    val id: Long,
    val name: String,
    val material: String?,
    val size: String?,
    val weight: String?,
    val stock: Int,
    val price: Long,
    val image: String?
)
