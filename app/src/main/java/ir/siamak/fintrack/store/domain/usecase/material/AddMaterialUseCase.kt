package ir.siamak.fintrack.store.domain.usecase.material
import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.domain.repository.MaterialRepository
import ir.siamak.fintrack.store.core.validation.StoreValidators
class AddMaterialUseCase(private val repository: MaterialRepository) {
    suspend operator fun invoke(material: RawMaterial) {
        require(StoreValidators.material(material) == null) { StoreValidators.material(material).orEmpty() }
        repository.save(material)
    }
}
