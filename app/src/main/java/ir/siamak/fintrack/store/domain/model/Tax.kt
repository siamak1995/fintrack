package ir.siamak.fintrack.store.domain.model

import kotlinx.serialization.Serializable

/** Percentage tax applied after a sale discount. */
@Serializable
data class Tax(val percent: Int = 0)
