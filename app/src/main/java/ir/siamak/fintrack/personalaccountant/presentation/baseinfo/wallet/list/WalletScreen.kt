package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.wallet.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.wallet.card.WalletCard
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard
import ir.siamak.fintrack.personalaccountant.presentation.components.FTTopBar
import ir.siamak.fintrack.personalaccountant.presentation.theme.ErrorRed
import ir.siamak.fintrack.personalaccountant.presentation.theme.PrimaryBlue
import ir.siamak.fintrack.personalaccountant.presentation.theme.Success

/**
 * UI اصلي صفحه ليست حساب‌ها.
 *
 * اين تابع فقط مسئول نمايش state و ارسال eventهاي UI به بيرون است
 * و هيچ وابستگي مستقيمي به ViewModel ندارد.
 *
 * وضعيت‌هاي قابل نمايش:
 * - loading
 * - error
 * - empty
 * - content
 *
 * در حالت content، کارت‌ها به‌صورت عمودي زير هم نمايش داده مي‌شوند
 * و هر کارت از swipe براي عمليات زير پشتيباني مي‌کند:
 * - کشيدن به راست: ويرايش
 * - کشيدن به چپ: حذف
 *
 * @param uiState وضعيت نمايشي صفحه
 * @param onAddWalletClick رويداد افزودن حساب جديد
 * @param onEditWalletClick رويداد ويرايش حساب با شناسه آن
 * @param onDeleteWalletClick رويداد حذف حساب
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
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = uiState.wallets,
                        key = { wallet -> wallet.id }
                    ) { wallet ->
                        WalletSwipeCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletSwipeCard(
    wallet: Wallet,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEditClick()
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onDeleteClick()
                    false
                }

                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            WalletSwipeBackground(
                dismissValue = dismissState.targetValue
            )
        },
        content = {
            WalletCard(
                wallet = wallet,
                onClick = onEditClick
            )
        }
    )
}

@Composable
private fun WalletSwipeBackground(
    dismissValue: SwipeToDismissBoxValue
) {
    val isEdit = dismissValue == SwipeToDismissBoxValue.StartToEnd
    val backgroundColor = if (isEdit) Success else ErrorRed
    val icon = if (isEdit) Icons.Default.Edit else Icons.Default.DeleteOutline
    val text = if (isEdit) "ویرایش" else "حذف"
    val alignment = if (isEdit) Alignment.CenterStart else Alignment.CenterEnd
    val horizontalPadding = if (isEdit) {
        Modifier.padding(start = 24.dp)
    } else {
        Modifier.padding(end = 24.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        FTCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp)
            ) {
                Box(
                    modifier = horizontalPadding.align(alignment),
                    contentAlignment = alignment
                ) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEdit) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = backgroundColor
                            )
                            Text(
                                text = "  $text",
                                color = backgroundColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else {
                            Text(
                                text = "$text  ",
                                color = backgroundColor,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = backgroundColor
                            )
                        }
                    }
                }
            }
        }
    }
}

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
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(4.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

