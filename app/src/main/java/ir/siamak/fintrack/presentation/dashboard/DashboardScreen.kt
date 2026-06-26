package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.*
import ir.siamak.fintrack.presentation.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddWalletClick: () -> Unit,
    onWalletClick: (Long) -> Unit,
    onAddTransactionClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            // هدر رو آوردیم اینجا که ثابت بمونه و با اسکرول غیب نشه
                FTTopBar(
                    title = " سلام کابر عزیز! 👋 حسابدارت منتظرت بود.",
                    subtitle = "امروز وضعیت جیبت چطوره؟",
                    actionIcon = Icons.Default.Notifications,
                    onActionClick = { /* نوتیفیکیشن‌ها */ },
                    modifier = Modifier.padding(16.dp)
                )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWalletClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزودن")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- کارت موجودی کل (ویترین اصلی) ---
            item {
                MainBalanceCard(totalBalance = state.totalBalance)
            }

            // --- بخش عملیات سریع (دکمه‌های دسترسی سریع) ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionItem(
                        title = "تراکنش جدید",
                        icon = Icons.Default.ReceiptLong,
                        color = PrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onAddTransactionClick
                    )
                    QuickActionItem(
                        title = "افزودن حساب",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = Success,
                        modifier = Modifier.weight(1f),
                        onClick = onAddWalletClick
                    )
                }
            }

            // --- بخش حساب‌ها (اسلایدر یا لیست کوتاه) ---
            item {
                SectionHeader(
                    title = "حساب‌های من",
                    actionLabel = "مشاهده همه",
                    onActionClick = { /* ناوبری به لیست حساب‌ها */ }
                )
            }

            if (state.wallets.isEmpty()) {
                item {
                    EmptyState(
                        title = "هنوز حسابی نداری!",
                        description = "برای شروع، اولین حسابت رو بساز."
                    )
                }
            } else {
                // فقط ۳ تای اول رو نشون میدیم
                items(state.wallets.take(3)) { wallet ->
                    WalletItem(wallet = wallet, onClick = { onWalletClick(wallet.id) })
                }
            }

            // --- بخش قسط‌ها یا گزارشات (فعلاً بصورت Placeholder برای زیبایی) ---
            item {
                SectionHeader(title = "یادآور قسط‌ها", actionLabel = "بیشتر")
            }

            item {
                FTCard {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EventNote, contentDescription = null, tint = ErrorRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("فعلاً قسط سررسید شده‌ای نداری. خیالت راحت! 😎", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }


            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MainBalanceCard(totalBalance: Double) {
    FTCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "موجودی کل فعلی",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            MoneyText(
                amount = totalBalance,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FTCard(
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onActionClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onActionClick?.invoke() }
            )
        }
    }
}

// تابع کمکی برای رنگ‌ها که توی کد قبلی هم داشتیم
private fun parseWalletColor(colorHex: String): Color {
    return try { Color(android.graphics.Color.parseColor(colorHex)) } catch (_: Exception) { PrimaryBlue }
}

@Composable
fun WalletItem(wallet: Wallet, onClick: () -> Unit) {
    FTCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small).background(parseWalletColor(wallet.color).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = parseWalletColor(wallet.color))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = wallet.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(text = wallet.currency.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            MoneyText(amount = wallet.balance, style = MaterialTheme.typography.bodyLarge, color = if (wallet.balance >= 0) Success else ErrorRed)
        }
    }
}
