package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** Identifies the shop whose accounting data is being managed. */
@Serializable
data class Store(
    val id: Long = 0,
    val name: String,
    val brand: String = "",
    val logoPath: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val description: String? = null,
    val brandId: String? = null,
    val startsOn: String? = null,
    val endsOn: String? = null,
    val settlement: StoreSettlementAccount? = null,
    val brandImagePath: String? = null
)

@Serializable data class StoreSettlementAccount(val contextId: Long, val walletId: Long, val memberId: Long)
@Serializable data class ExhibitionPeriod(val start: String, val end: String)
data class FinanceChoice(val id: Long, val contextId: Long, val name: String)
data class StoreFinanceDirectory(val wallets: List<FinanceChoice> = emptyList(), val members: List<FinanceChoice> = emptyList(), val posted: Map<String,Long> = emptyMap(), val conflicts: Set<String> = emptySet())
