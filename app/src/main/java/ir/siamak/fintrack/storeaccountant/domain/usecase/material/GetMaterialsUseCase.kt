package ir.siamak.fintrack.storeaccountant.domain.usecase.material

import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial
import ir.siamak.fintrack.storeaccountant.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMaterialsUseCase @Inject constructor(
    private val repository: MaterialRepository
) {
    operator fun invoke(): Flow<List<RawMaterial>> {
        return repository.getAllMaterials()
    }
}
