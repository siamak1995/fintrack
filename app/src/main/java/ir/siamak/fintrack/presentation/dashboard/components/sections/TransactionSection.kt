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
//import ir.siamak.fintrack.data.model.Transaction
//import ir.siamak.fintrack.presentation.dashboard.EmptySectionText
//import ir.siamak.fintrack.presentation.dashboard.components.items.TransactionItem
//
//@Composable
//fun TransactionSection(
//    transactions: List<Transaction>
//) {
//
//    Column {
//
//        Text(
//            text = "آخرین تراکنش‌ها",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(bottom = 12.dp)
//        )
//
//        if (transactions.isEmpty()) {
//
//            EmptySectionText(
//                text = "تراکنشی ثبت نشده است."
//            )
//
//            return
//        }
//
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
//            userScrollEnabled = false
//        ) {
//
//            items(transactions) {
//
//                TransactionItem(it)
//
//            }
//
//        }
//
//    }
//
//}