package ir.siamak.fintrack.store.domain.usecase.store

import ir.siamak.fintrack.store.domain.model.Store
import ir.siamak.fintrack.store.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow

/** Streams the current store profile. */
class GetStoreUseCase(private val repository: StoreRepository) { operator fun invoke(): Flow<Store?> = repository.observeStore() }
