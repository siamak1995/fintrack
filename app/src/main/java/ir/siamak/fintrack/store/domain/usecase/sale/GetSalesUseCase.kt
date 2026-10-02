package ir.siamak.fintrack.store.domain.usecase.sale

import ir.siamak.fintrack.store.domain.model.Sale
import ir.siamak.fintrack.store.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow

/** Streams the latest sales. */
class GetSalesUseCase(private val repository: SaleRepository) { operator fun invoke(): Flow<List<Sale>> = repository.observeAll() }
