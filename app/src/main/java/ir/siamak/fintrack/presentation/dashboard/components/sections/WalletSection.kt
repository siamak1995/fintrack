package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.dashboard.components.items.WalletItem

/**
 * بخش نمایش کیف پول‌ها در داشبورد.
 */
@Composable
fun WalletSection(
    wallets: List<Wallet>,
    onWalletClick: (Long) -> Unit
) {
    Column {
        SectionHeader(title = "حساب‌های من")

        if (wallets.isEmpty()) {
            EmptySectionText("کیف پولی ثبت نشده است.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(wallets) { wallet ->
                    WalletItem(
                        wallet = wallet,
                        onClick = { onWalletClick(wallet.id) }
                    )
                }
            }
        }
    }
}

