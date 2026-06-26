package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.EmptyState
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.components.StatCard
import ir.siamak.fintrack.presentation.components.SummaryCard
import ir.siamak.fintrack.presentation.theme.AppTheme
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.PrimaryBlue
import ir.siamak.fintrack.presentation.theme.Success

/**
 * صفحه داشبورد اپلیکیشن.
 *
 * این صفحه نمای کلی وضعیت مالی کاربر را نمایش می‌دهد و در فاز فعلی شامل:
 * - هدر خوش‌آمدگویی
 * - کارت موجودی کل
 * - آمار سریع
 * - عملیات سریع
 * - پیش‌نمایش لیست حساب‌ها
 *
 * داده‌های این صفحه از [DashboardViewModel] دریافت می‌شود.
 *
 * @param viewModel ویومدل صفحه داشبورد
 * @param onAddWalletClick اکشن افزودن حساب
 * @param onWalletClick اکشن کلیک روی یک حساب
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddWalletClick: () -> Unit,
    onWalletClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWalletClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزودن حساب"
                )
            }
        }
    ) { innerPadding ->
        DashboardContent(
            state = state,
            onRefresh = { viewModel.onEvent(DashboardEvent.RefreshData) },
            onAddWalletClick = onAddWalletClick,
            onWalletClick = onWalletClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * محتوای اصلی صفحه داشبورد.
 *
 * @param state وضعیت فعلی UI
 * @param onRefresh رویداد تازه‌سازی اطلاعات
 * @param onAddWalletClick اکشن افزودن حساب
 * @param onWalletClick اکشن ورود به جزئیات حساب
 * @param modifier استایل اضافی
 */
@Composable
private fun DashboardContent(
    state: DashboardState,
    onRefresh: () -> Unit,
    onAddWalletClick: () -> Unit,
    onWalletClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = AppTheme.dimens.screenHorizontalPadding,
            vertical = AppTheme.dimens.screenVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
    ) {
        item {
            FTTopBar(
                title = "سلام 👋",
                subtitle = "نمای کلی وضعیت مالی شما",
                actionIcon = Icons.Default.Sync,
                actionContentDescription = "تازه‌سازی",
                onActionClick = onRefresh
            )
        }

        item {
            TotalBalanceCard(totalBalance = state.totalBalance)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
            ) {
                StatCard(
                    title = "تعداد حساب‌ها",
                    value = state.wallets.size.toString(),
                    icon = Icons.Default.AccountBalanceWallet,
                    iconBackground = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "موجودی کل",
                    amount = state.totalBalance,
                    icon = Icons.Default.Info,
                    iconBackground = Success,
                    amountColor = Success,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle(title = "عملیات سریع")
        }

        item {
            QuickActionsRow(
                onAddAccountClick = onAddWalletClick,
                onRefreshClick = onRefresh
            )
        }

        item {
            SectionTitle(
                title = "حساب‌ها",
                actionLabel = if (state.wallets.isNotEmpty()) "افزودن" else null,
                onActionClick = onAddWalletClick
            )
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        state.error?.let { errorMessage ->
            item {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorRed
                )
            }
        }

        if (!state.isLoading && state.wallets.isEmpty() && state.error == null) {
            item {
                EmptyState(
                    title = "هنوز حسابی ثبت نشده",
                    description = "برای شروع مدیریت مالی، اولین حساب خود را اضافه کن."
                )
            }
        } else {
            items(
                items = state.wallets.take(5),
                key = { wallet -> wallet.id }
            ) { wallet ->
                WalletItem(
                    wallet = wallet,
                    onClick = { onWalletClick(wallet.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TotalBalanceCard(
    totalBalance: Double,
    modifier: Modifier = Modifier
) {
    FTCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(AppTheme.spacing.small))

                Column {
                    Text(
                        text = "موجودی کل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "جمع موجودی همه حساب‌ها",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.small))

            MoneyText(
                amount = totalBalance,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAddAccountClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
    ) {
        QuickActionCard(
            title = "افزودن حساب",
            icon = Icons.Default.Add,
            onClick = onAddAccountClick,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            title = "بروزرسانی",
            icon = Icons.Default.Sync,
            onClick = onRefreshClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FTCard(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun WalletItem(
    wallet: Wallet,
    onClick: () -> Unit
) {
    FTCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(parseWalletColor(wallet.color).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = parseWalletColor(wallet.color)
                )
            }

            Spacer(modifier = Modifier.width(AppTheme.spacing.medium))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)
            ) {
                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = wallet.currency.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                MoneyText(
                    amount = wallet.balance,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (wallet.balance >= 0) Success else ErrorRed
                )

                Spacer(modifier = Modifier.width(AppTheme.spacing.extraSmall))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * تبدیل رشته هگز رنگ به Color برای نمایش در UI.
 *
 * اگر مقدار رنگ نامعتبر باشد، رنگ پیش‌فرض برگردانده می‌شود.
 */
private fun parseWalletColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (_: Exception) {
        PrimaryBlue
    }
}
