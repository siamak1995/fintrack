package ir.siamak.fintrack.storeaccountant.domain.usecase.material

import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial
import ir.siamak.fintrack.storeaccountant.domain.repository.MaterialRepository
import javax.inject.Inject

class UpdateMaterialUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    suspend operator fun invoke(material: RawMaterial) {
        repository.updateMaterial(material)
    }
}
