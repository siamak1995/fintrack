package ir.siamak.fintrack.store.domain.usecase.material

import ir.siamak.fintrack.store.core.validation.StoreValidators
import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.domain.repository.MaterialRepository

/** Updates a valid raw-material record. */
class UpdateMaterialUseCase(private val repository: MaterialRepository) { suspend operator fun invoke(material: RawMaterial) { require(material.id > 0 && StoreValidators.material(material) == null); repository.save(material) } }
