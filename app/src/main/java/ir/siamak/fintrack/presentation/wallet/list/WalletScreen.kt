package ir.siamak.fintrack.presentation.wallet.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.FTTopBar
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.PrimaryBlue
import ir.siamak.fintrack.presentation.theme.Success
import androidx.compose.material3.Scaffold

/**
 * UI اصلی صفحه لیست حساب‌ها.
 *
 * این تابع فقط مسئول نمایش state و ارسال eventهای UI به بیرون است
 * و هیچ وابستگی مستقیمی به ViewModel ندارد.
 *
 * وضعیت‌های قابل نمایش:
 * - loading
 * - error
 * - empty
 * - content
 *
 * @param uiState وضعیت نمایشی صفحه
 * @param onAddWalletClick رویداد افزودن حساب جدید
 * @param onEditWalletClick رویداد ویرایش حساب با شناسه آن
 * @param onDeleteWalletClick رویداد حذف حساب
 */
@Composable
fun WalletScreen(
    uiState: WalletUiState,
    onAddWalletClick: () -> Unit,
    onEditWalletClick: (Long) -> Unit,
    onDeleteWalletClick: (Wallet) -> Unit
) {
    Scaffold(
        topBar = {
            FTTopBar(
                title = "حساب‌های بانکی",
                subtitle = "مدیریت و ویرایش حساب‌ها",
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddWalletClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("حساب جدید") }
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                WalletMessageState(
                    modifier = Modifier.padding(padding),
                    title = "خطا در دریافت اطلاعات",
                    message = uiState.error
                )
            }

            uiState.wallets.isEmpty() -> {
                WalletMessageState(
                    modifier = Modifier.padding(padding),
                    title = "هنوز حسابی ثبت نشده",
                    message = "برای شروع، یک حساب جدید اضافه کن."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.wallets,
                        key = { wallet -> wallet.id }
                    ) { wallet ->
                        WalletListCard(
                            wallet = wallet,
                            onEditClick = { onEditWalletClick(wallet.id) },
                            onDeleteClick = { onDeleteWalletClick(wallet) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * کارت نمایش هر حساب در لیست.
 *
 * @param wallet مدل حساب
 * @param onEditClick callback ویرایش
 * @param onDeleteClick callback حذف
 */
@Composable
fun WalletListCard(
    wallet: Wallet,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    FTCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(parseColor(wallet.color).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = parseColor(wallet.color)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "واحد پول: ${wallet.currency.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                MoneyText(
                    amount = wallet.balance,
                    color = if (wallet.balance >= 0) Success else ErrorRed
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("حذف")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ویرایش")
                }
            }
        }
    }
}

/**
 * کامپوننت عمومی برای نمایش پیام‌های خالی/خطا در صفحه حساب‌ها.
 *
 * @param title عنوان وضعیت
 * @param message توضیح تکمیلی وضعیت
 * @param modifier modifier نمایشی
 */
@Composable
private fun WalletMessageState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FTCard(
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * تبدیل رشته رنگ ذخیره‌شده به Color قابل استفاده در Compose.
 *
 * اگر مقدار رنگ نامعتبر باشد، رنگ پیش‌فرض PrimaryBlue برگردانده می‌شود.
 *
 * @param colorHex مقدار hex رنگ مانند #2196F3
 */
private fun parseColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (_: Exception) {
        PrimaryBlue
    }
}
