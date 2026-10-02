package ir.siamak.fintrack.store.domain.usecase.store

import ir.siamak.fintrack.store.domain.model.Store
import ir.siamak.fintrack.store.domain.repository.StoreRepository

/** Validates and persists a store profile. */
class SaveStoreUseCase(private val repository: StoreRepository) { suspend operator fun invoke(store: Store) { require(store.name.isNotBlank()) { "Store name is required" }; repository.save(store) } }
