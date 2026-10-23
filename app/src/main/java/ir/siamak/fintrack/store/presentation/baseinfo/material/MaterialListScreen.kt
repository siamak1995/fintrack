package ir.siamak.fintrack.store.presentation.baseinfo.material
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.presentation.baseinfo.BaseInfoHubScreen
import androidx.hilt.navigation.compose.hiltViewModel
@Composable fun MaterialListScreen() = BaseInfoHubScreen(initialTab = "material", vm = hiltViewModel<MaterialListViewModel>())
