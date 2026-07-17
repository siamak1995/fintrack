package ir.siamak.fintrack.presentation.dashboard.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DashboardStatCard(

    title: String,

    value: String,

    icon: ImageVector,

    color: Color,

    modifier: Modifier = Modifier,

    onClick: (() -> Unit)? = null

) {

    Surface(

        modifier = modifier
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },

        shape = RoundedCornerShape(20.dp),

        tonalElevation = 3.dp

    ) {

        Column(

            modifier = Modifier.padding(18.dp)

        ) {

            Box(

                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color.copy(.15f),
                        RoundedCornerShape(14.dp)
                    ),

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = color

                )

            }

            Spacer(Modifier.height(16.dp))

            Text(

                title,

                style = MaterialTheme.typography.labelMedium,

                color = MaterialTheme.colorScheme.outline

            )

            Spacer(Modifier.height(4.dp))

            Text(

                value,

                style = MaterialTheme.typography.headlineSmall

            )

        }

    }

}