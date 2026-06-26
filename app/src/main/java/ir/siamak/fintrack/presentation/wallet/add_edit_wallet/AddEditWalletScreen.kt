package ir.siamak.fintrack.presentation.wallet.add_edit_wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward // برای دکمه بازگشت در محیط فارسی
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.presentation.components.FTTopBar

@Composable
fun AddEditWalletScreen(
    viewModel: AddEditWalletViewModel = hiltViewModel(),
    walletId: Long?,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // لود کردن اطلاعات اگر در حالت ویرایش هستیم
    LaunchedEffect(walletId) {
        walletId?.let {
            viewModel.onEvent(AddEditWalletEvent.LoadWallet(it))
        }
    }

    // خروج از صفحه بعد از ذخیره موفق
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            FTTopBar(
                title = if (walletId == null) "حساب جدید" else "ویرایش حساب",
                subtitle = "اطلاعات حساب بانکی خود را وارد کنید",
                actionIcon = Icons.Default.ArrowForward, // دکمه بازگشت
                onActionClick = onBack,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp) // پدینگ بیشتر برای ظاهر بهتر
        ) {
            // فیلد نام حساب
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEditWalletEvent.EnteredName(it)) },
                label = { Text("نام حساب (مثلاً بانک ملت)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // فیلد موجودی
            OutlinedTextField(
                value = state.balance,
                onValueChange = { viewModel.onEvent(AddEditWalletEvent.EnteredBalance(it)) },
                label = { Text("موجودی فعلی (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                placeholder = { Text("0") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // دکمه ذخیره بزرگ و شیک
            Button(
                onClick = { viewModel.onEvent(AddEditWalletEvent.SaveWallet) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !state.isLoading && state.name.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ذخیره تغییرات", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
