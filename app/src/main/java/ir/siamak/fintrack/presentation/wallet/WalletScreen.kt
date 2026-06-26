package ir.siamak.fintrack.presentation.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet

/**
 * صفحه نمایش کیف پول‌ها.
 *
 * این صفحه داده‌های مورد نیاز را از [WalletViewModel] دریافت می‌کند
 * و وضعیت‌های مختلف مانند بارگذاری، خطا و لیست داده‌ها را نمایش می‌دهد.
 *
 * @param viewModel ویومدل مربوط به مدیریت داده‌های کیف پول
 */
@Composable
fun WalletScreen(
    viewModel: WalletViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Wallets",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                viewModel.insertWallet(
                    Wallet(
                        id = 0L,
                        name = "کیف پول تست",
                        balance = 100000.0,
                        color = "#4CAF50"
                    )
                )
            }
        ) {
            Text(text = "افزودن کیف پول تست")
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "خطا",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.wallets) { wallet ->
                        WalletItem(
                            wallet = wallet,
                            onDeleteClick = { viewModel.deleteWallet(wallet) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * آیتم نمایشی مربوط به یک کیف پول در لیست.
 *
 * @param wallet اطلاعات کیف پول برای نمایش
 * @param onDeleteClick تابعی که هنگام کلیک روی حذف اجرا می‌شود
 */
@Composable
fun WalletItem(
    wallet: Wallet,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = wallet.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Balance: ${wallet.balance}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Color: ${wallet.color}",
                style = MaterialTheme.typography.bodySmall
            )

            Button(onClick = onDeleteClick) {
                Text(text = "حذف")
            }
        }
    }
}
