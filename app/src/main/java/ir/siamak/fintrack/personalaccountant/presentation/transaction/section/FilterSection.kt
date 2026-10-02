package ir.siamak.fintrack.personalaccountant.presentation.transaction.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Tag
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.presentation.transaction.TransactionListFilter

/**
 * Displays transaction filters for members, wallets and tags.
 *
 * This section keeps filter changes outside the ViewModel until the caller invokes apply,
 * so it can be used with a draft filter state in the transaction list screen.
 */
@OptIn(ExperimentalLayoutApi::class)
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
    onApplyClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

        if (!isExpanded) return@Column

        FilterGroupTitle(text = "عضو")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter.selectedMemberId == null,
                onClick = { onMemberSelected(null) },
                label = { Text("همه اعضا") }
            )

            members.forEach { member ->
                FilterChip(
                    selected = filter.selectedMemberId == member.id,
                    onClick = { onMemberSelected(member.id) },
                    label = { Text(member.name) }
                )
            }
        }

        HorizontalDivider()

        FilterGroupTitle(text = "کیف پول")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter.selectedWalletId == null,
                onClick = { onWalletSelected(null) },
                label = { Text("همه کیف پول‌ها") }
            )

            wallets.forEach { wallet ->
                FilterChip(
                    selected = filter.selectedWalletId == wallet.id,
                    onClick = { onWalletSelected(wallet.id) },
                    label = { Text(wallet.name) }
                )
            }
        }

        HorizontalDivider()

        FilterGroupTitle(text = "تگ")

        if (tags.isEmpty()) {
            Text(
                text = "تگی برای این نوع تراکنش وجود ندارد.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter.selectedTagIds.isEmpty(),
                    onClick = {
                        filter.selectedTagIds.forEach(onTagToggled)
                    },
                    label = { Text("همه تگ‌ها") }
                )

                tags.forEach { tag ->
                    FilterChip(
                        selected = tag.id in filter.selectedTagIds,
                        onClick = { onTagToggled(tag.id) },
                        label = { Text(tag.name) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onClearClicked,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAltOff,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text("پاک کردن")
            }

            Button(
                onClick = onApplyClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text("اعمال فیلتر")
            }
        }
    }
}

@Composable
private fun FilterGroupTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
}

