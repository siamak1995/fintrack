package ir.siamak.fintrack.store.domain.usecase.material

import ir.siamak.fintrack.store.domain.repository.MaterialRepository

/** Deletes a raw-material record. */
class DeleteMaterialUseCase(private val repository: MaterialRepository) { suspend operator fun invoke(id: Long) { require(id > 0); repository.delete(id) } }
