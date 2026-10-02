package ir.siamak.fintrack.personalaccountant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * کارت خلاصه اطلاعات مالی
 */
@Composable
fun SummaryCard(
    title:String,
    amount:Double,
    icon:ImageVector,
    iconBackground:Color,
    amountColor:Color=MaterialTheme.colorScheme.primary,
    modifier:Modifier=Modifier
){

    FTCard(
        modifier=modifier.width(165.dp)
    ){

        Column(
            modifier=Modifier.padding(16.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement=Arrangement.Center
        ){

            Box(
                modifier=Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground.copy(alpha=.15f)),
                contentAlignment=Alignment.Center
            ){

                Icon(
                    imageVector=icon,
                    contentDescription=null,
                    tint=iconBackground
                )

            }

            Spacer(Modifier.height(12.dp))

            Text(
                text=title,
                style=MaterialTheme.typography.labelMedium,
                color=MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            MoneyText(
                amount=amount,
                color=amountColor,
                style=MaterialTheme.typography.titleLarge
            )

        }

    }

}
