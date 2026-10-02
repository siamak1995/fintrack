package ir.siamak.fintrack.personalaccountant.presentation.dashboard.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard
import java.time.LocalTime

@Composable
fun GreetingSection(
    userName:String,
    insight:String
){

    val greeting=when(LocalTime.now().hour){
        in 5..11->"صبح بخیر"
        in 12..16->"ظهر بخیر"
        in 17..20->"عصر بخیر"
        else->"شب بخیر"
    }

    FTCard{

        Column(
            modifier=Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(.12f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp)
        ){

            Icon(
                imageVector=Icons.Default.WavingHand,
                contentDescription=null,
                tint=MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text="$greeting، $userName 👋",
                style=MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text="به داشبورد مدیریت مالی خوش آمدید",
                style=MaterialTheme.typography.bodyMedium,
                color=MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                shape=RoundedCornerShape(14.dp),
                tonalElevation=2.dp,
                color=MaterialTheme.colorScheme.surfaceVariant
            ){

                Column(
                    Modifier.padding(14.dp)
                ){

                    Text(
                        text="تحلیل امروز",
                        style=MaterialTheme.typography.titleSmall
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text=insight,
                        style=MaterialTheme.typography.bodyMedium
                    )

//                    Spacer(Modifier.height(10.dp))
//
//                    Text(
//                        text="وضعیت مالی : $financialHealth",
//                        style=MaterialTheme.typography.labelLarge,
//                        color=MaterialTheme.colorScheme.primary
//                    )

                }

            }

        }

    }

}
