package ir.siamak.fintrack.personalaccountant.presentation.landing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GreetingCard(
    userName: String,
    welcome: String
) {

    Column {

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = welcome,
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )

    }

}
