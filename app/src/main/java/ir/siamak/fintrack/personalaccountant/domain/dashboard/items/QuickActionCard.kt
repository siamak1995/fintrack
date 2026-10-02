package ir.siamak.fintrack.personalaccountant.domain.dashboard.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import ir.siamak.fintrack.personalaccountant.presentation.components.FTCard

@Composable
fun QuickActionCard(
    title:String,
    icon:ImageVector,
    color:Color,
    onClick:()->Unit
){

    FTCard(
        modifier=Modifier.clickable(onClick=onClick)
    ){

        Column(
            modifier=Modifier.padding(16.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement=Arrangement.Center
        ){

            Box(
                modifier=Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha=.15f)),
                contentAlignment=Alignment.Center
            ){

                Icon(
                    imageVector=icon,
                    contentDescription=null,
                    tint=color
                )

            }

            Text(
                text=title,
                modifier=Modifier.padding(top=12.dp),
                style=MaterialTheme.typography.labelMedium
            )

        }

    }

}
