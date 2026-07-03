package ir.siamak.fintrack.domain.dashboard.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.components.MoneyText
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: Transaction
) {

    val color =
        when (transaction.type) {

            TransactionType.INCOME -> Success

            TransactionType.EXPENSE -> ErrorRed

            TransactionType.TRANSFER ->
                MaterialTheme.colorScheme.primary
        }

    val icon =
        when (transaction.type) {

            TransactionType.INCOME ->
                Icons.Default.CallReceived

            TransactionType.EXPENSE ->
                Icons.Default.CallMade

            TransactionType.TRANSFER ->
                Icons.Default.SwapHoriz
        }

    val amount =
        if (transaction.type == TransactionType.EXPENSE)
            -transaction.amount
        else
            transaction.amount

    val date =
        SimpleDateFormat(
            "yyyy/MM/dd",
            Locale.getDefault()
        ).format(Date(transaction.date))

    FTCard {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(

                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(.12f)),

                contentAlignment = Alignment.Center

            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )

            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = transaction.categoryName,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = transaction.note ?: "بدون توضیح",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

            }

            MoneyText(
                amount = amount,
                showSign = true,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}