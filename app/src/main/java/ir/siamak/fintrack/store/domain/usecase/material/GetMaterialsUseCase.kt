package ir.siamak.fintrack.store.domain.usecase.material

import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow

/** Streams raw-material inventory. */
class GetMaterialsUseCase(private val repository: MaterialRepository) { operator fun invoke(): Flow<List<RawMaterial>> = repository.observeAll() }
