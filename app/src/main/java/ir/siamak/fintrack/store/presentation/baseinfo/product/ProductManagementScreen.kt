package ir.siamak.fintrack.store.presentation.baseinfo.product
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.domain.model.Product
import ir.siamak.fintrack.store.presentation.baseinfo.CatalogEditor

@Composable fun ProductManagementScreen(value: Product?, busy: Boolean, error: String?, onSave: (Product) -> Unit, onDismiss: () -> Unit) =
    CatalogEditor("product",value,busy,error,{ onSave(it as Product) },onDismiss)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun ProductManagementScreenPreview() { androidx.compose.material3.MaterialTheme { ProductManagementScreen(null,false,null,{}, {}) } }
