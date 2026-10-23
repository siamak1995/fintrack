package ir.siamak.fintrack.store.presentation.baseinfo.material
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ir.siamak.fintrack.store.presentation.StoreFeatureViewModel
import ir.siamak.fintrack.store.domain.usecase.store.*

@HiltViewModel
class MaterialListViewModel @Inject constructor(observe: ObserveStoreWorkspaceUseCase, commands: StoreCommandsUseCase) : StoreFeatureViewModel(observe, commands)
