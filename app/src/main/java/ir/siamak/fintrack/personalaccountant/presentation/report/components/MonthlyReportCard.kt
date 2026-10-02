package ir.siamak.fintrack.personalaccountant.presentation.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.domain.report.MonthlyReportItem
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard
import ir.siamak.fintrack.personalaccountant.presentation.components.MoneyText
import ir.siamak.fintrack.personalaccountant.presentation.theme.ErrorRed
import ir.siamak.fintrack.personalaccountant.presentation.theme.Success

@Composable
fun MonthlyReportCard(

    item: MonthlyReportItem

) {

    FTCard {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(

                text = item.month,

                style = MaterialTheme.typography.titleMedium

            )

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Column {

                    Text("درآمد")

                    MoneyText(

                        amount = item.income,

                        color = Success

                    )

                }

                Column {

                    Text("هزینه")

                    MoneyText(

                        amount = item.expense,

                        color = ErrorRed

                    )

                }

                Column {

                    Text("پس‌انداز")

                    MoneyText(

                        amount = item.saving,

                        color = MaterialTheme.colorScheme.primary

                    )

                }

            }

        }

    }

}
