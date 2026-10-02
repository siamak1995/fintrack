package ir.siamak.fintrack.personalaccountant.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FTButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = true,
    icon: ImageVector? = null
) {

    Button(
        onClick = onClick,
        modifier = if (fullWidth) modifier.then(Modifier) else modifier,
        enabled = enabled && !loading
    ) {

        if (loading) {

            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.size(8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (icon != null) {

                Icon(
                    imageVector = icon,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(8.dp))
            }

            Text(text = text)
        }
    }
}
