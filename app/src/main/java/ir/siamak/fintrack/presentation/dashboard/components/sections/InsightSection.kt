package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.components.FTCard

@Composable
fun InsightSection(
    insight:String
){

    FTCard{

        Column(
            modifier=Modifier.padding(20.dp)
        ){

            Row(
                verticalAlignment=Alignment.CenterVertically
            ){

                Icon(
                    imageVector=Icons.Default.AutoGraph,
                    contentDescription=null,
                    tint=MaterialTheme.colorScheme.primary
                )

                Text(
                    text="تحلیل هوشمند",
                    modifier=Modifier.padding(start=8.dp),
                    style=MaterialTheme.typography.titleMedium
                )

            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(.08f))
                    .padding(16.dp),
                verticalAlignment=Alignment.Top
            ){

                Icon(
                    imageVector=Icons.Default.Lightbulb,
                    contentDescription=null,
                    tint=MaterialTheme.colorScheme.primary
                )

                Text(
                    text=insight,
                    modifier=Modifier.padding(start=12.dp),
                    style=MaterialTheme.typography.bodyLarge
                )

            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment=Alignment.CenterVertically
            ){

                Icon(
                    imageVector=Icons.Default.TipsAndUpdates,
                    contentDescription=null,
                    tint=MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text="پیشنهاد",
                    modifier=Modifier.padding(start=8.dp),
                    style=MaterialTheme.typography.titleSmall
                )

            }

            Spacer(Modifier.height(8.dp))

            Text(
                text="ثبت روزانه تراکنش‌ها باعث دقیق‌تر شدن تحلیل‌های مالی خواهد شد.",
                style=MaterialTheme.typography.bodyMedium,
                color=MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }

}