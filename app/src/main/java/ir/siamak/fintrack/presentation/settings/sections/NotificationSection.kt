package ir.siamak.fintrack.presentation.settings.sections

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
fun NotificationSection(
    notificationEnabled: Boolean,
    installmentReminder: Boolean,
    dailyReminder: Boolean,
    budgetReminder: Boolean,
    onNotificationChanged: (Boolean) -> Unit,
    onInstallmentChanged: (Boolean) -> Unit,
    onDailyChanged: (Boolean) -> Unit,
    onBudgetChanged: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "اعلان‌ها",
                style = MaterialTheme.typography.titleMedium
            )

            SectionSwitchRow(
                title = "فعال‌سازی اعلان‌ها",
                checked = notificationEnabled,
                onCheckedChange = onNotificationChanged
            )

            SectionSwitchRow(
                title = "یادآوری اقساط",
                checked = installmentReminder,
                onCheckedChange = onInstallmentChanged
            )

            SectionSwitchRow(
                title = "یادآوری روزانه",
                checked = dailyReminder,
                onCheckedChange = onDailyChanged
            )

            SectionSwitchRow(
                title = "هشدار بودجه",
                checked = budgetReminder,
                onCheckedChange = onBudgetChanged
            )
        }
    }
}
