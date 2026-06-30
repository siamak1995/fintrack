package ir.siamak.fintrack.presentation.installment.add_edit_installment

import androidx.compose.foundation.layout.*
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
@Composable
fun AddEditInstallmentsScreen(
    installmentId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditInstallmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            FTTextField(
                value = state.title,

                onValueChange = {

                    viewModel.onEvent(

                        AddEditInstallmentEvent.TitleChanged(it)

                    )

                },

                label = "عنوان قسط"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FTTextField(
                value = state.totalAmount,
                onValueChange = {

                    viewModel.onEvent(

                        AddEditInstallmentEvent.TotalAmountChanged(it)

                    )

                },
                label = "مبلغ کل"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FTTextField(
                value = state.paidAmount,
                onValueChange = {

                    viewModel.onEvent(

                        AddEditInstallmentEvent.PaidAmountChanged(it)

                    )

                },
                label = "مبلغ پرداخت شده"
            )

            Spacer(modifier = Modifier.height(24.dp))

            FTButton(
                text = "ذخیره",
                onClick = {

                    viewModel.onEvent(
                        AddEditInstallmentEvent.Save
                    )

                }
            )
        }
    }
}
