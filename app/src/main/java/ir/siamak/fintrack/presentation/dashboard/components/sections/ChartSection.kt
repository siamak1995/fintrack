package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard
import ir.siamak.fintrack.presentation.dashboard.FinancialHealth
import ir.siamak.fintrack.presentation.dashboard.components.SectionHeader
import ir.siamak.fintrack.presentation.theme.ErrorRed
import ir.siamak.fintrack.presentation.theme.Success

@Composable
fun ChartSection(
    spending:Float,
    saving:Float,
    health:FinancialHealth
){

    FTCard{

        Column(
            modifier=Modifier.padding(20.dp)
        ){

            SectionHeader(
                title="وضعیت مالی این ماه",
                icon=Icons.Default.PieChart
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text="درصد پس‌انداز",
                style=MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress={saving/100f},
                modifier=Modifier.fillMaxWidth(),
                color=Success
            )

            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .padding(top=4.dp),
                horizontalArrangement=Arrangement.SpaceBetween
            ){

                Text("${saving.toInt()} %")

                Text(
                    text=when{
                        saving>=70f->"عالی"
                        saving>=50f->"خوب"
                        saving>=30f->"متوسط"
                        else->"ضعیف"
                    },
                    color=Success
                )

            }

            Spacer(Modifier.height(22.dp))

            Text(
                text="درصد هزینه",
                style=MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress={spending/100f},
                modifier=Modifier.fillMaxWidth(),
                color=ErrorRed
            )

            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .padding(top=4.dp),
                horizontalArrangement=Arrangement.SpaceBetween
            ){

                Text("${spending.toInt()} %")

                Text(
                    text=when{
                        spending<40f->"کم"
                        spending<70f->"طبیعی"
                        spending<90f->"زیاد"
                        else->"بحرانی"
                    },
                    color=ErrorRed
                )

            }

            Spacer(Modifier.height(24.dp))

            FinancialHealthCard(health)

        }

    }

}

@Composable
private fun FinancialHealthCard(
    health:FinancialHealth
){

    val title:String
    val color:Color

    when(health){

        FinancialHealth.EXCELLENT->{
            title="سلامت مالی عالی"
            color=Success
        }

        FinancialHealth.GOOD->{
            title="سلامت مالی خوب"
            color=Color(0xFF2563EB)
        }

        FinancialHealth.WARNING->{
            title="نیاز به مدیریت هزینه"
            color=Color(0xFFF59E0B)
        }

        FinancialHealth.DANGER->{
            title="وضعیت بحرانی"
            color=ErrorRed
        }

    }

    Text(
        text=title,
        color=color,
        style=MaterialTheme.typography.titleMedium
    )

}