package ir.siamak.fintrack.presentation.installment.add_edit_installment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.siamak.fintrack.presentation.components.FTTextField
import ir.siamak.fintrack.presentation.components.FTButton

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

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            FTTextField(
                value = "",
                onValueChange = {},
                label = "عنوان قسط"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FTTextField(
                value = "",
                onValueChange = {},
                label = "مبلغ کل"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FTTextField(
                value = "",
                onValueChange = {},
                label = "مبلغ پرداخت شده"
            )

            Spacer(modifier = Modifier.height(24.dp))

            FTButton(
                text = "ذخیره",
                onClick = { }
            )
        }
    }
}
