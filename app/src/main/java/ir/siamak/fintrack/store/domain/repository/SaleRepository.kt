package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.Sale
import kotlinx.coroutines.flow.Flow

/** Sales data boundary. */
interface SaleRepository { fun observeAll(): Flow<List<Sale>>; suspend fun create(sale: Sale): Long }
