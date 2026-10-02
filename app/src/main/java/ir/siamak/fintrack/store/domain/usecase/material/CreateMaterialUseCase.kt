package ir.siamak.fintrack.store.domain.usecase.material

import ir.siamak.fintrack.store.core.validation.StoreValidators
import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.domain.repository.MaterialRepository

/** Validates and saves a raw material. */
class CreateMaterialUseCase(private val repository: MaterialRepository) { suspend operator fun invoke(material: RawMaterial) { require(StoreValidators.material(material) == null) { StoreValidators.material(material).orEmpty() }; repository.save(material) } }
