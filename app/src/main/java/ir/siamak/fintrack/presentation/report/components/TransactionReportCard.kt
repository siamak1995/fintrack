package ir.siamak.fintrack.presentation.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionReportCard(

    transaction: Transaction

) {

    val income = transaction.type == TransactionType.INCOME

    val color =
        if (income)
            Color(0xFFE8F5E9)
        else
            Color(0xFFFFEBEE)

    FTCard {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Icon(

                if (income)
                    Icons.Default.ArrowDownward
                else
                    Icons.Default.ArrowUpward,

                null

            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    transaction.note,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    transaction.categoryName,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(

                    SimpleDateFormat(
                        "yyyy/MM/dd",
                        Locale.getDefault()
                    ).format(Date(transaction.date))

                )

            }
            MoneyText(
                amount = transaction.amount,
                color = if (income) Color(0xFF2E7D32) else Color.Red,
                showSign = true
            )
        }

    }

}