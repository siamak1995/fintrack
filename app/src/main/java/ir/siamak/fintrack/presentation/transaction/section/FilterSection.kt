package ir.siamak.fintrack.presentation.transaction.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.transaction.TransactionListFilter

@Composable
fun FilterSection(
    filter: TransactionListFilter,
    members: List<Member>,
    wallets: List<Wallet>,
    tags: List<Tag>,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onMemberSelected: (Long?) -> Unit,
    onWalletSelected: (Long?) -> Unit,
    onTagToggled: (Long) -> Unit,
    onClearClicked: () -> Unit,
    onApplyClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onExpandToggle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "انتخاب فیلتر",
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = null
            )
        }

        if (isExpanded) {
            Text(
                text = "تعداد اعضا: ${members.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "تعداد کیف پول‌ها: ${wallets.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "تعداد تگ‌ها: ${tags.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedButton(
                onClick = onClearClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAltOff,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(" پاک کردن")
            }

            Button(
                onClick = onApplyClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("اعمال فیلتر")
            }
        }
    }
}
