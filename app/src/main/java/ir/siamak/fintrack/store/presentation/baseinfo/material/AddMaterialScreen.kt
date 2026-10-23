package ir.siamak.fintrack.store.presentation.baseinfo.material
import androidx.compose.runtime.Composable
import ir.siamak.fintrack.store.domain.model.RawMaterial
import ir.siamak.fintrack.store.presentation.baseinfo.CatalogEditor

@Composable fun AddMaterialScreen(value: RawMaterial?, busy: Boolean, error: String?, onSave: (RawMaterial) -> Unit, onDismiss: () -> Unit) =
    CatalogEditor("material",value,busy,error,{ onSave(it as RawMaterial) },onDismiss)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable private fun AddMaterialScreenPreview() { androidx.compose.material3.MaterialTheme { AddMaterialScreen(null,false,null,{}, {}) } }
