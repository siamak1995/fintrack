package ir.siamak.fintrack.storeaccountant.domain.usecase.store

import ir.siamak.fintrack.storeaccountant.domain.model.Store
import ir.siamak.fintrack.storeaccountant.domain.repository.StoreRepository
import javax.inject.Inject

class SaveStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(store: Store) = repository.saveStore(store)
}
