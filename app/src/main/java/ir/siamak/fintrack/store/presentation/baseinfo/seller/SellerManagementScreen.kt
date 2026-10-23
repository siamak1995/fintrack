package ir.siamak.fintrack.store.presentation.baseinfo.seller
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.domain.model.Seller
import ir.siamak.fintrack.store.presentation.baseinfo.CatalogEditor

@Composable fun SellerManagementScreen(value: Seller?, busy: Boolean, error: String?, onSave: (Seller) -> Unit, onDismiss: () -> Unit) =
    CatalogEditor("seller",value,busy,error,{ onSave(it as Seller) },onDismiss)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun SellerManagementScreenPreview() { androidx.compose.material3.MaterialTheme { SellerManagementScreen(null,false,null,{}, {}) } }
