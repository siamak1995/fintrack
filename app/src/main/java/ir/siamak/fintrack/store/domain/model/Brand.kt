package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Brand(val id: String = java.util.UUID.randomUUID().toString(), val name: String, val image: String? = null)

@Serializable
data class StoreProfiles(
    val activeStoreId: Long? = null,
    val brands: List<Brand> = emptyList(),
    val signatures: Map<Long, String> = emptyMap(),
    val cards: Map<Long, String> = emptyMap(),
    val accounts: Map<Long, String> = emptyMap(),
    val storeBrands: Map<Long, String> = emptyMap()
)
