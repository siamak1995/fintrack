//package ir.siamak.fintrack.presentation.dashboard.components.sections
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBalanceWallet
//import androidx.compose.material.icons.filled.ArrowCircleDown
//import androidx.compose.material.icons.filled.ArrowCircleUp
//import androidx.compose.material.icons.filled.Paid
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import ir.siamak.fintrack.presentation.components.FTCard
//import ir.siamak.fintrack.presentation.components.MoneyText
//import ir.siamak.fintrack.presentation.theme.ErrorRed
//import ir.siamak.fintrack.presentation.theme.Success
//
///**
// * کارت خلاصه وضعیت مالی داشبورد
// */
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//fun SummarySection(
//
//    totalBalance: Double,
//
//    walletBalance: Double,
//
//    income: Double,
//
//    expense: Double
//
//) {
//
//    Column {
//
//        Text(
//
//            text = "خلاصه وضعیت مالی",
//
//            style = MaterialTheme.typography.titleLarge,
//
//            modifier = Modifier.padding(bottom = 12.dp)
//
//        )
//
//        FlowRow(
//
//            modifier = Modifier.fillMaxWidth(),
//
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//
//        ) {
//
//            SummaryCard(
//
//                title = "موجودی فعلی",
//
//                amount = totalBalance,
//
//                color = MaterialTheme.colorScheme.primary,
//
//                icon = {
//
//                    Icon(
//
//                        Icons.Default.Paid,
//
//                        null,
//
//                        tint = MaterialTheme.colorScheme.primary
//
//                    )
//
//                }
//
//            )
//
//            SummaryCard(
//
//                title = "موجودی حساب‌ها",
//
//                amount = walletBalance,
//
//                color = Color(0xFF7C3AED),
//
//                icon = {
//
//                    Icon(
//
//                        Icons.Default.AccountBalanceWallet,
//
//                        null,
//
//                        tint = Color(0xFF7C3AED)
//
//                    )
//
//                }
//
//            )
//
//            SummaryCard(
//
//                title = "درآمد ماه",
//
//                amount = income,
//
//                color = Success,
//
//                icon = {
//
//                    Icon(
//
//                        Icons.Default.ArrowCircleDown,
//
//                        null,
//
//                        tint = Success
//
//                    )
//
//                }
//
//            )
//
//            SummaryCard(
//
//                title = "هزینه ماه",
//
//                amount = expense,
//
//                color = ErrorRed,
//
//                icon = {
//
//                    Icon(
//
//                        Icons.Default.ArrowCircleUp,
//
//                        null,
//
//                        tint = ErrorRed
//
//                    )
//
//                }
//
//            )
//
//        }
//
//    }
//
//}
//
//@Composable
//private fun SummaryCard(
//
//    title: String,
//
//    amount: Double,
//
//    color: Color,
//
//    icon: @Composable () -> Unit
//
//) {
//
//    FTCard(
//
//        modifier = Modifier.width(170.dp)
////        modifier = Modifier.fillMaxWidth(.48f)
//
//
//    ) {
//
//        Column(
//
//            modifier = Modifier.padding(18.dp),
//
//            horizontalAlignment = Alignment.CenterHorizontally
//
//        ) {
//
//            icon()
//
//            Text(
//
//                text = title,
//
//                modifier = Modifier.padding(top = 10.dp),
//
//                style = MaterialTheme.typography.labelLarge
//
//            )
//
//            MoneyText(
//
//                amount = amount,
//
//                color = color,
//
//                style = MaterialTheme.typography.titleLarge
//
//            )
//
//        }
//
//    }
//
//}