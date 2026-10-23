package ir.siamak.fintrack.store.presentation.baseinfo.store
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.domain.model.Store
import ir.siamak.fintrack.store.presentation.baseinfo.CatalogEditor

@Composable fun StoreInfoScreen(value: Store?, busy: Boolean, error: String?, onSave: (Store) -> Unit, onDismiss: () -> Unit) =
    CatalogEditor("store",value,busy,error,{ onSave(it as Store) },onDismiss)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun StoreInfoScreenPreview() { androidx.compose.material3.MaterialTheme { StoreInfoScreen(null,false,null,{}, {}) } }
