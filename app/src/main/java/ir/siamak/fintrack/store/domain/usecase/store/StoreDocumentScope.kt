package ir.siamak.fintrack.store.domain.usecase.store

import ir.siamak.fintrack.store.domain.model.Invoice
import ir.siamak.fintrack.store.domain.model.Store

/** v1 snapshots without a store profile (id=0) remain visible in the original/default store. */
fun List<Invoice>.forStore(active: Store?, stores: List<Store>): List<Invoice> = filter {
    it.store.id == active?.id || (it.store.id == 0L && (active == null || active.id == stores.firstOrNull()?.id))
}
