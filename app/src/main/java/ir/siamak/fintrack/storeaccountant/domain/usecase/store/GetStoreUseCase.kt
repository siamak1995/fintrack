package ir.siamak.fintrack.storeaccountant.domain.usecase.store

import ir.siamak.fintrack.storeaccountant.domain.model.Store
import ir.siamak.fintrack.storeaccountant.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(): Flow<Store?> = repository.getStore()
}
