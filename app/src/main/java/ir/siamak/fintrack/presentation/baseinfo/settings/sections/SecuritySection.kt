package ir.siamak.fintrack.presentation.baseinfo.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SecuritySection(
    biometricEnabled: Boolean,
    pinEnabled: Boolean,
    onBiometricChanged: (Boolean) -> Unit,
    onPinChanged: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "امنیت",
                style = MaterialTheme.typography.titleMedium
            )

            SectionSwitchRow(
                title = "ورود با اثر انگشت",
                checked = biometricEnabled,
                onCheckedChange = onBiometricChanged
            )

            SectionSwitchRow(
                title = "قفل با PIN",
                checked = pinEnabled,
                onCheckedChange = onPinChanged
            )
        }
    }
}
