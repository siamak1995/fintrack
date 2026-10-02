package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.sections

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

/**
 * بخش تنظیمات امنیتی برنامه شامل PIN و احراز هویت زیست‌سنجی.
 */
@Composable
fun SecuritySection(
    biometricEnabled: Boolean,
    pinEnabled: Boolean,
    isBiometricHardwareAvailable: Boolean,
    onBiometricChanged: (Boolean) -> Unit,
    onPinChanged: (Boolean) -> Unit
) {
    val canUseBiometric = pinEnabled && isBiometricHardwareAvailable

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
                title = "قفل با PIN",
                checked = pinEnabled,
                onCheckedChange = onPinChanged
            )

            SectionSwitchRow(
                title = "ورود با اثر انگشت",
                checked = biometricEnabled && canUseBiometric,
                enabled = canUseBiometric,
                onCheckedChange = onBiometricChanged
            )
        }
    }
}

