package ir.siamak.fintrack.presentation.dashboard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet

/**
 * Screen:
 * لایه نمایش است. فقط وظیفه دارد State را نشان دهد و وقتی کاربر تعاملی کرد، Event ارسال کند.
 *
 *
 * @Composable:
 *  به کامپایلر می‌گوید این تابع برای ساختن رابط کاربری (UI) است.
 *  توابع کامپوزبل مثل آجرهای لگو هستند که برای چیدن ظاهر اپلیکیشن کنار هم قرار می‌گیرند.
 *
 * @OptIn
 * در کاتلین، وقتی گوگل یا توسعه‌دهندگان کتابخانه‌ها یک قابلیت جدید (مثل یک کامپوننت در Material 3) اضافه می‌کنند که
 * هنوز ممکن است در نسخه‌های بعدی تغییر کند یا به اصطلاح “آزمایشی” (Experimental) باشد، آن را با یک Annotation علامت‌گذاری می‌کنند.
 *
 * صفحه اصلی داشبورد که وضعیت مالی کلی و لیست کیف‌پول‌ها را نمایش می‌دهد.
 *
 * @param viewModel ویومدل مدیریت‌کننده وضعیت این صفحه.
 * @param onAddWalletClick تابع Callback هنگام کلیک روی دکمه افزودن کیف‌پول.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddWalletClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("FinTrack") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddWalletClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Wallet")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TotalBalanceCard(balance = state.totalBalance)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "My Wallets",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(state.wallets) { wallet ->
                    WalletCard(wallet = wallet)
                }
            }
        }
    }
}

/**
 * کارت نمایش دهنده موجودی کل.
 */
@Composable
fun TotalBalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Total Balance", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "$${String.format("%,.2f", balance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/**
 * کارت نمایش دهنده یک کیف‌پول خاص.
 */
@Composable
fun WalletCard(wallet: Wallet) {
    Card(modifier = Modifier.size(width = 160.dp, height = 100.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = wallet.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "$${wallet.balance}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
