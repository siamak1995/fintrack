package ir.siamak.fintrack.presentation.dashboard.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GreetingSection(
    userName: String,
    insight: String
) {

    Column {

        Text(
            text = "سلام $userName 👋",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = insight,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}