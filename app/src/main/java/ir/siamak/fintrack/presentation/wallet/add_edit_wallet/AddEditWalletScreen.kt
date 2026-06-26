package ir.siamak.fintrack.presentation.wallet.add_edit_wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * صفحه افزودن یا ویرایش کیف ‌پول.
 *
 * @param viewModel ویومدل مربوط به این صفحه که توسط Hilt تزریق می‌شود.
 * @param onBack تابعی که هنگام اتمام عملیات یا انصراف برای بازگشت به صفحه قبل صدا زده می‌شود.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWalletScreen(
    viewModel: AddEditWalletViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // مانیتور کردن وضعیت ذخیره‌سازی برای خروج از صفحه
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("افزودن کیف ‌پول جدید") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEditWalletEvent.EnteredName(it)) },
                label = { Text("نام حساب یا بانک") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.balance,
                onValueChange = { viewModel.onEvent(AddEditWalletEvent.EnteredBalance(it)) },
                label = { Text("موجودی فعلی (تومان)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onEvent(AddEditWalletEvent.SaveWallet) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.name.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ذخیره کیف ‌پول")
                }
            }
        }
    }
}
