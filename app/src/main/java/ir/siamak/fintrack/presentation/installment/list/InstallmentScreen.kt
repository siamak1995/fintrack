package ir.siamak.fintrack.presentation.installment.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.presentation.components.EmptyState

/**
 * صفحه نمایش لیست اقساط.
 */
@Composable
fun InstallmentScreen(
    state: InstallmentUiState,
    onAddInstallmentClick: () -> Unit,
    onEditInstallmentClick: (Long) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddInstallmentClick
            ) {
                Text("+")
            }
        }
    ) { padding ->

        if (state.installments.isEmpty()) {

            EmptyState(
                title = "هنوز قسطی ثبت نشده است",
                description = "برای شروع، اولین قسط را ثبت کن.",
                modifier = Modifier.padding(padding)
            )


        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                items(state.installments) { installment ->

                    InstallmentItem(
                        installment = installment,
                        onClick = { onEditInstallmentClick(installment.id) }
                    )

                }
            }
        }
    }
}

/**
 * آیتم نمایش یک قسط در لیست.
 */
@Composable
private fun InstallmentItem(
    installment: Installment,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = installment.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "مبلغ کل: ${installment.totalAmount}"
            )

            Text(
                text = "پرداخت شده: ${installment.paidAmount}"
            )
        }
    }
}
