package ir.siamak.fintrack.presentation.installment.add_edit_installment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.presentation.components.FTTextField
import ir.siamak.fintrack.presentation.components.FTButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

/**
 * صفحه افزودن یا ویرایش قسط.
 *
 * اگر installmentId null باشد یعنی حالت Add
 * در غیر این صورت حالت Edit است.
 */
@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInstallmentsScreen(
    installmentId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditInstallmentsViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val isEdit = installmentId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit)
                            "ویرایش قسط"
                        else
                            "افزودن قسط"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 عنوان
            FTTextField(
                value = state.title,
                onValueChange = {
                    viewModel.onEvent(AddEditInstallmentEvent.TitleChanged(it))
                },
                label = "عنوان قسط"
            )

            // 🔹 مبلغ کل
            FTTextField(
                value = state.totalAmountFormatted,
                onValueChange = {
                    viewModel.onEvent(AddEditInstallmentEvent.TotalAmountChanged(it))
                },
                label = "مبلغ کل (تومان)"
            )

            // 🔹 مبلغ پرداخت شده
            FTTextField(
                value = state.paidAmountFormatted,
                onValueChange = {
                    viewModel.onEvent(AddEditInstallmentEvent.PaidAmountChanged(it))
                },
                label = "مبلغ پرداخت شده (تومان)"
            )

            Spacer(Modifier.height(12.dp))
            state.error?.let {

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
            FTButton(
                text = "ذخیره",
                enabled = !state.paidExceedsTotal && state.title.isNotBlank(),
                onClick = {
                    viewModel.onEvent(AddEditInstallmentEvent.Save)
                }
            )
        }
    }
}