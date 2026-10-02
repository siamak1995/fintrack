package ir.siamak.fintrack.store.domain.model

/** A person allowed to issue sales on behalf of a store. */
data class Seller(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String? = null,
    val description: String? = null
) {
    val fullName: String get() = listOf(firstName, lastName).filter(String::isNotBlank).joinToString(" ")
}
