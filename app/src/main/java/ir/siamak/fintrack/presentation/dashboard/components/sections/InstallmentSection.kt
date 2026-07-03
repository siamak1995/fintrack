//package ir.siamak.fintrack.presentation.dashboard.components.sections
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import ir.siamak.fintrack.data.model.Installment
//import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
//import ir.siamak.fintrack.presentation.dashboard.components.items.InstallmentItem
//
//@Composable
//fun InstallmentSection(
//
//    installments: List<Installment>
//
//) {
//
//    Column {
//
//        Text(
//
//            text = "اقساط",
//
//            style = MaterialTheme.typography.titleLarge,
//
//            modifier = Modifier.padding(bottom = 12.dp)
//
//        )
//
//        if (installments.isEmpty()) {
//
//            EmptySectionText(
//
//                text = "قسطی ثبت نشده است."
//
//            )
//
//            return
//
//        }
//
//        LazyColumn(
//
//            modifier = Modifier.fillMaxWidth(),
//
//            userScrollEnabled = false
//
//        ) {
//
//            items(installments) {
//
//                InstallmentItem(it)
//
//            }
//
//        }
//
//    }
//
//}